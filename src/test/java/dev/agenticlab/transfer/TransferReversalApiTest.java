package dev.agenticlab.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Testes do agente para a Story 2.1 (AD-8): HTTP contra PostgreSQL real. Cobrem casos além de
 * {@code TransferReversalReferenceTest}: ausência de {@code Location}, representação igual à da
 * consulta, identificador malformado, limite exato de saldo no destino, nova tentativa depois de
 * uma rejeição, isolamento entre Transferências e estornos concorrentes com transferências nos
 * dois sentidos entre as mesmas contas.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferReversalApiTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    private static final long TIMEOUT_SECONDS = 60;

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void reversalHasNoLocationAndReturnsTheQueriedRepresentation() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");
        UUID transferId = transferId(transfer(sourceId, destinationId, "30.00"));
        Map<String, Object> beforeReversal = getTransfer(transferId).getBody();

        ResponseEntity<Map<String, Object>> reversed = reverse(transferId);

        assertThat(reversed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reversed.getHeaders().getLocation()).isNull();
        assertThat(reversed.getBody()).isEqualTo(getTransfer(transferId).getBody());
        assertThat(reversed.getBody().get("createdAt")).isEqualTo(beforeReversal.get("createdAt"));
    }

    @Test
    void rejectsMalformedTransferIdentifier() {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/transfers/not-a-uuid/reversal", HttpMethod.POST, null, JSON_OBJECT);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("code")).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void reversesWhenDestinationHasExactlyTheAmount() {
        UUID sourceId = createAccount("50.00");
        UUID destinationId = createAccount("0.00");
        UUID transferId = transferId(transfer(sourceId, destinationId, "50.00"));

        assertThat(reverse(transferId).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(balanceOf(sourceId)).isEqualByComparingTo("50.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("0.00");
    }

    @Test
    void rejectedReversalCanSucceedAfterDestinationIsFundedAgain() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");
        UUID otherId = createAccount("100.00");
        UUID transferId = transferId(transfer(sourceId, destinationId, "100.00"));
        transferId(transfer(destinationId, otherId, "30.00"));

        ResponseEntity<Map<String, Object>> rejected = reverse(transferId);
        assertThat(rejected.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(rejected.getBody().get("code")).isEqualTo("INSUFFICIENT_FUNDS");

        transferId(transfer(otherId, destinationId, "30.00"));
        ResponseEntity<Map<String, Object>> reversed = reverse(transferId);

        assertThat(reversed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reversed.getBody().get("status")).isEqualTo("REVERSED");
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("100.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("0.00");
        assertThat(balanceOf(otherId)).isEqualByComparingTo("100.00");
    }

    @Test
    void reversalAffectsOnlyTheTargetTransfer() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");
        UUID firstId = transferId(transfer(sourceId, destinationId, "10.00"));
        UUID secondId = transferId(transfer(sourceId, destinationId, "20.00"));

        assertThat(reverse(firstId).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(getTransfer(firstId).getBody().get("status")).isEqualTo("REVERSED");
        assertThat(getTransfer(secondId).getBody().get("status")).isEqualTo("COMPLETED");
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("80.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("20.00");
    }

    /**
     * AD-4: o estorno bloqueia as contas na mesma ordem que uma Transferência. Estornos e
     * transferências nos dois sentidos entre as mesmas contas, ao mesmo tempo, terminam sem
     * deadlock (nenhum 5xx), preservam a soma dos saldos e não deixam saldo negativo.
     */
    @Test
    void concurrentReversalsAndTransfersInBothDirectionsDoNotDeadlock() throws Exception {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("1000.00");
        List<UUID> toReverse = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            toReverse.add(transferId(transfer(accountA, accountB, "10.00")));
            toReverse.add(transferId(transfer(accountB, accountA, "10.00")));
        }

        List<Callable<ResponseEntity<Map<String, Object>>>> requests = new ArrayList<>();
        for (UUID transferId : toReverse) {
            requests.add(() -> reverse(transferId));
        }
        for (int i = 0; i < 5; i++) {
            requests.add(() -> transfer(accountA, accountB, "1.00"));
            requests.add(() -> transfer(accountB, accountA, "1.00"));
        }
        List<ResponseEntity<Map<String, Object>>> responses = runConcurrently(requests);

        assertThat(responses)
                .allSatisfy(response -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue());
        for (UUID transferId : toReverse) {
            assertThat(getTransfer(transferId).getBody().get("status")).isEqualTo("REVERSED");
        }
        assertThat(balanceOf(accountA)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("1000.00");
    }

    private List<ResponseEntity<Map<String, Object>>> runConcurrently(
            List<Callable<ResponseEntity<Map<String, Object>>>> requests) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(requests.size());
        try {
            List<Future<ResponseEntity<Map<String, Object>>>> futures = new ArrayList<>();
            for (Callable<ResponseEntity<Map<String, Object>>> request : requests) {
                futures.add(
                        executor.submit(
                                () -> {
                                    start.await();
                                    return request.call();
                                }));
            }
            start.countDown();

            List<ResponseEntity<Map<String, Object>>> responses = new ArrayList<>();
            for (Future<ResponseEntity<Map<String, Object>>> future : futures) {
                responses.add(future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            }
            return responses;
        } finally {
            executor.shutdownNow();
        }
    }

    private UUID createAccount(String initialBalance) {
        Map<String, Object> requestBody = Map.of("initialBalance", new BigDecimal(initialBalance));
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
        return UUID.fromString(String.valueOf(response.getBody().get("id")));
    }

    private BigDecimal balanceOf(UUID accountId) {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, accountId);
        return new BigDecimal(String.valueOf(response.getBody().get("balance")));
    }

    private ResponseEntity<Map<String, Object>> transfer(
            UUID sourceId, UUID destinationId, String amount) {
        Map<String, Object> requestBody =
                Map.of(
                        "sourceAccountId", sourceId,
                        "destinationAccountId", destinationId,
                        "amount", new BigDecimal(amount));
        return restTemplate.exchange(
                "/transfers", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
    }

    private UUID transferId(ResponseEntity<Map<String, Object>> created) {
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return UUID.fromString(String.valueOf(created.getBody().get("id")));
    }

    private ResponseEntity<Map<String, Object>> reverse(UUID transferId) {
        return restTemplate.exchange(
                "/transfers/{id}/reversal", HttpMethod.POST, null, JSON_OBJECT, transferId);
    }

    private ResponseEntity<Map<String, Object>> getTransfer(UUID transferId) {
        return restTemplate.exchange("/transfers/{id}", HttpMethod.GET, null, JSON_OBJECT, transferId);
    }
}
