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
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Especificação executável da Story 1.4 / S4 (CC-EXP-03): saldo não negativo
 * sob concorrência, contra PostgreSQL real, com várias threads (AD-8).
 *
 * <p>Estes testes fazem parte da fotografia inicial de {@code freeze/exp-03}
 * (ver {@code docs/lab/02-claude-code-exp-03.md}). Nela, o teste do
 * {@code CHECK (balance >= 0)} nasce vermelho; os dois testes de concorrência
 * já passam.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferConcurrencyReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    private static final int THREADS = 20;
    private static final long TIMEOUT_SECONDS = 60;
    private static final String CHECK_VIOLATION = "23514";

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private JdbcTemplate jdbcTemplate;

    /** AC1: saldo nunca negativo e soma aceita nunca maior que o saldo inicial. */
    @Test
    void concurrentTransfersNeverOverdrawSource() throws Exception {
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal amount = new BigDecimal("10.00");
        UUID sourceId = createAccount(initialBalance);
        UUID destinationId = createAccount(BigDecimal.ZERO);
        long transfersBefore = countTransfersFrom(sourceId);

        List<Callable<ResponseEntity<Map<String, Object>>>> requests = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            requests.add(() -> transfer(sourceId, destinationId, amount));
        }
        List<ResponseEntity<Map<String, Object>>> responses = runConcurrently(requests);

        BigDecimal acceptedSum = BigDecimal.ZERO;
        int accepted = 0;
        for (ResponseEntity<Map<String, Object>> response : responses) {
            if (response.getStatusCode() == HttpStatus.CREATED) {
                accepted++;
                acceptedSum = acceptedSum.add(amount);
            } else {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("code")).isEqualTo("INSUFFICIENT_FUNDS");
            }
        }

        BigDecimal sourceBalance = balanceOf(sourceId);
        assertThat(sourceBalance).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(acceptedSum).isLessThanOrEqualTo(initialBalance);
        assertThat(sourceBalance).isEqualByComparingTo(initialBalance.subtract(acceptedSum));
        assertThat(balanceOf(destinationId)).isEqualByComparingTo(acceptedSum);
        assertThat(countTransfersFrom(sourceId) - transfersBefore).isEqualTo(accepted);
    }

    /** AC2: transferências em sentidos opostos entre as mesmas contas terminam sem deadlock. */
    @Test
    void opposingConcurrentTransfersCompleteWithoutDeadlock() throws Exception {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal amount = new BigDecimal("1.00");
        UUID accountA = createAccount(initialBalance);
        UUID accountB = createAccount(initialBalance);

        List<Callable<ResponseEntity<Map<String, Object>>>> requests = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            requests.add(() -> transfer(accountA, accountB, amount));
            requests.add(() -> transfer(accountB, accountA, amount));
        }
        List<ResponseEntity<Map<String, Object>>> responses = runConcurrently(requests);

        assertThat(responses)
                .allSatisfy(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED));
        assertThat(balanceOf(accountA)).isEqualByComparingTo(initialBalance);
        assertThat(balanceOf(accountB)).isEqualByComparingTo(initialBalance);
    }

    /** AC3: o banco impõe {@code CHECK (balance >= 0)}. */
    @Test
    void balanceCheckConstraintRejectsNegativeBalance() {
        UUID accountId = createAccount(new BigDecimal("10.00"));

        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        "UPDATE account SET balance = -0.01 WHERE id = ?", accountId))
                .satisfies(ex -> assertThat(sqlState(ex)).isEqualTo(CHECK_VIOLATION));
        assertThat(balanceOf(accountId)).isEqualByComparingTo("10.00");
    }

    /**
     * Dispara todas as requisições ao mesmo tempo e espera todas terminarem.
     * Falha se alguma não terminar no prazo.
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

    private UUID createAccount(BigDecimal initialBalance) {
        Map<String, Object> requestBody = Map.of("initialBalance", initialBalance);
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
        return UUID.fromString(String.valueOf(response.getBody().get("id")));
    }

    private ResponseEntity<Map<String, Object>> transfer(
            UUID sourceId, UUID destinationId, BigDecimal amount) {
        Map<String, Object> requestBody =
                Map.of(
                        "sourceAccountId", sourceId,
                        "destinationAccountId", destinationId,
                        "amount", amount);
        return restTemplate.exchange(
                "/transfers", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
    }

    private BigDecimal balanceOf(UUID accountId) {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, accountId);
        return new BigDecimal(String.valueOf(response.getBody().get("balance")));
    }

    private long countTransfersFrom(UUID sourceId) {
        Long count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM transfer WHERE source_account_id = ?", Long.class, sourceId);
        return count == null ? 0 : count;
    }
}
