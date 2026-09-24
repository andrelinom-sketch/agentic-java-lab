package dev.agenticlab.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Especificação executável da Story 2.1 (MULTI-AGENT-EXP-01): estorno de uma
 * Transferência concluída, contra PostgreSQL real (AD-8). A preparação usa só a
 * API pública; o banco é lido diretamente apenas para contar Transferências e
 * para verificar a restrição de status do esquema.
 *
 * <p>Na fotografia inicial do freeze, todos os testes nascem vermelhos, porque
 * {@code POST /transfers/{id}/reversal} ainda não existe e o esquema ainda não
 * restringe o status.
 *
 * <p>A ordem de bloqueio (Transferência antes das Contas) e a atomicidade
 * interna do estorno não são observáveis por HTTP de forma determinística;
 * ficam para a revisão (ver {@code docs/lab/05-multi-agent-exp-01.md}).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferReversalReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    private static final int THREADS = 20;
    private static final long TIMEOUT_SECONDS = 60;
    private static final String CHECK_VIOLATION = "23514";

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private JdbcTemplate jdbcTemplate;

    /**
     * AC1: estorno válido devolve os saldos, responde 200 com a Transferência no
     * formato de {@code POST /transfers} e status {@code REVERSED}, e não cria
     * outra Transferência.
     */
    @Test
    void reversesCompletedTransfer() {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("500.00");
        Map<String, Object> created = createTransfer(accountA, accountB, "100.00");
        UUID transferId = idOf(created);
        assertThat(balanceOf(accountA)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("600.00");
        long transfersBefore = countTransfersBetween(accountA, accountB);

        ResponseEntity<Map<String, Object>> response = reverse(transferId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body)
                .containsOnlyKeys(
                        "id", "sourceAccountId", "destinationAccountId", "amount", "status", "createdAt");
        assertThat(String.valueOf(body.get("id"))).isEqualTo(transferId.toString());
        assertThat(String.valueOf(body.get("sourceAccountId"))).isEqualTo(accountA.toString());
        assertThat(String.valueOf(body.get("destinationAccountId"))).isEqualTo(accountB.toString());
        assertThat(new BigDecimal(String.valueOf(body.get("amount")))).isEqualByComparingTo("100.00");
        assertThat(body.get("status")).isEqualTo("REVERSED");

        assertThat(balanceOf(accountA)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("500.00");
        assertThat(countTransfersBetween(accountA, accountB)).isEqualTo(transfersBefore);
    }

    /** AC2: a consulta de uma Transferência estornada mostra {@code REVERSED}. */
    @Test
    void getsReversedTransfer() {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("500.00");
        UUID transferId = idOf(createTransfer(accountA, accountB, "100.00"));
        assertThat(reverse(transferId).getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> response = getTransfer(transferId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(String.valueOf(body.get("id"))).isEqualTo(transferId.toString());
        assertThat(body.get("status")).isEqualTo("REVERSED");
    }

    /** AC3: um segundo estorno responde 422 {@code TRANSFER_ALREADY_REVERSED}, sem efeito. */
    @Test
    void rejectsSecondReversalWithoutEffect() {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("500.00");
        UUID transferId = idOf(createTransfer(accountA, accountB, "100.00"));
        assertThat(reverse(transferId).getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> response = reverse(transferId);

        assertProblem(response, HttpStatus.UNPROCESSABLE_ENTITY, "TRANSFER_ALREADY_REVERSED");
        assertThat(balanceOf(accountA)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("500.00");
        assertThat(statusOf(transferId)).isEqualTo("REVERSED");
    }

    /** AC4: estorno de id inexistente responde 404 {@code TRANSFER_NOT_FOUND}. */
    @Test
    void returnsNotFoundProblemForUnknownTransfer() {
        ResponseEntity<Map<String, Object>> response = reverse(UUID.randomUUID());

        assertProblem(response, HttpStatus.NOT_FOUND, "TRANSFER_NOT_FOUND");
    }

    /**
     * AC5: sem saldo suficiente no destino no momento do estorno, o estorno falha
     * com 422 {@code INSUFFICIENT_FUNDS}, a Transferência permanece
     * {@code COMPLETED} e nenhum saldo muda.
     */
    @Test
    void rejectsReversalWhenDestinationLacksFunds() {
        UUID accountA = createAccount("100.00");
        UUID accountB = createAccount("0.00");
        UUID accountC = createAccount("0.00");
        UUID transferId = idOf(createTransfer(accountA, accountB, "100.00"));
        createTransfer(accountB, accountC, "60.00");

        ResponseEntity<Map<String, Object>> response = reverse(transferId);

        assertProblem(response, HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_FUNDS");
        assertThat(statusOf(transferId)).isEqualTo("COMPLETED");
        assertThat(balanceOf(accountA)).isEqualByComparingTo("0.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("40.00");
        assertThat(balanceOf(accountC)).isEqualByComparingTo("60.00");
    }

    /**
     * AC6: entre estornos simultâneos da mesma Transferência, exatamente um tem
     * efeito; os demais respondem 422 {@code TRANSFER_ALREADY_REVERSED}.
     */
    @Test
    void concurrentReversalsTakeEffectExactlyOnce() throws Exception {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("500.00");
        UUID transferId = idOf(createTransfer(accountA, accountB, "100.00"));
        long transfersBefore = countTransfersBetween(accountA, accountB);

        List<Callable<ResponseEntity<Map<String, Object>>>> requests = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            requests.add(() -> reverse(transferId));
        }
        List<ResponseEntity<Map<String, Object>>> responses = runConcurrently(requests);

        int reversed = 0;
        for (ResponseEntity<Map<String, Object>> response : responses) {
            if (response.getStatusCode() == HttpStatus.OK) {
                reversed++;
            } else {
                assertProblem(response, HttpStatus.UNPROCESSABLE_ENTITY, "TRANSFER_ALREADY_REVERSED");
            }
        }
        assertThat(reversed).isEqualTo(1);
        assertThat(balanceOf(accountA)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(accountB)).isEqualByComparingTo("500.00");
        assertThat(statusOf(transferId)).isEqualTo("REVERSED");
        assertThat(countTransfersBetween(accountA, accountB)).isEqualTo(transfersBefore);
    }

    /** AC8: o esquema admite apenas {@code COMPLETED} e {@code REVERSED} como status. */
    @Test
    void statusCheckConstraintRejectsUnknownStatus() {
        UUID accountA = createAccount("100.00");
        UUID accountB = createAccount("0.00");
        UUID transferId = idOf(createTransfer(accountA, accountB, "10.00"));

        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        "UPDATE transfer SET status = 'PENDING' WHERE id = ?", transferId))
                .satisfies(ex -> assertThat(sqlState(ex)).isEqualTo(CHECK_VIOLATION));
        assertThat(statusOf(transferId)).isEqualTo("COMPLETED");
    }

    private static void assertProblem(
            ResponseEntity<Map<String, Object>> response, HttpStatus status, String code) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)).isTrue();
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(status.value());
        assertThat(body.get("code")).isEqualTo(code);
    }

    /**
     * Dispara todas as requisições ao mesmo tempo e espera todas terminarem.
     * Falha se alguma não terminar no prazo ou responder 5xx.
     */
    private List<ResponseEntity<Map<String, Object>>> runConcurrently(
            List<Callable<ResponseEntity<Map<String, Object>>>> requests) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        List<Callable<ResponseEntity<Map<String, Object>>>> gated = new ArrayList<>();
        for (Callable<ResponseEntity<Map<String, Object>>> request : requests) {
            gated.add(
                    () -> {
                        start.await();
                        return request.call();
                    });
        }

        ExecutorService executor = Executors.newFixedThreadPool(requests.size());
        try {
            List<Future<ResponseEntity<Map<String, Object>>>> futures = new ArrayList<>();
            for (Callable<ResponseEntity<Map<String, Object>>> task : gated) {
                futures.add(executor.submit(task));
            }
            start.countDown();

            List<ResponseEntity<Map<String, Object>>> responses = new ArrayList<>();
            for (Future<ResponseEntity<Map<String, Object>>> future : futures) {
                responses.add(future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            }
            assertThat(responses)
                    .allSatisfy(response -> assertThat(response.getStatusCode().is5xxServerError()).isFalse());
            return responses;
        } finally {
            executor.shutdownNow();
        }
    }

    private static String sqlState(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException && sqlException.getSQLState() != null) {
                return sqlException.getSQLState();
            }
        }
        return null;
    }

    private UUID createAccount(String initialBalance) {
        Map<String, Object> requestBody = Map.of("initialBalance", new BigDecimal(initialBalance));
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return UUID.fromString(String.valueOf(response.getBody().get("id")));
    }

    private Map<String, Object> createTransfer(UUID sourceId, UUID destinationId, String amount) {
        Map<String, Object> requestBody =
                Map.of(
                        "sourceAccountId", sourceId,
                        "destinationAccountId", destinationId,
                        "amount", new BigDecimal(amount));
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/transfers", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private static UUID idOf(Map<String, Object> transfer) {
        return UUID.fromString(String.valueOf(transfer.get("id")));
    }

    private ResponseEntity<Map<String, Object>> reverse(UUID transferId) {
        return restTemplate.exchange(
                "/transfers/{id}/reversal", HttpMethod.POST, null, JSON_OBJECT, transferId);
    }

    private ResponseEntity<Map<String, Object>> getTransfer(UUID transferId) {
        return restTemplate.exchange(
                "/transfers/{id}", HttpMethod.GET, null, JSON_OBJECT, transferId);
    }

    private String statusOf(UUID transferId) {
        ResponseEntity<Map<String, Object>> response = getTransfer(transferId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return String.valueOf(response.getBody().get("status"));
    }

    private BigDecimal balanceOf(UUID accountId) {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, accountId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return new BigDecimal(String.valueOf(response.getBody().get("balance")));
    }

    /** Transferências em qualquer sentido entre as duas Contas. */
    private long countTransfersBetween(UUID accountA, UUID accountB) {
        Long count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM transfer"
                                + " WHERE (source_account_id = ? AND destination_account_id = ?)"
                                + " OR (source_account_id = ? AND destination_account_id = ?)",
                        Long.class,
                        accountA,
                        accountB,
                        accountB,
                        accountA);
        return count == null ? 0 : count;
    }
}
