package dev.agenticlab.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testes do agente para a Story 1.3 (AD-8): HTTP contra PostgreSQL real. Cobrem casos além de
 * {@code TransferRejectionReferenceTest}: limites de valor, entrada malformada, precedência entre
 * violações simultâneas, independência da ordem de bloqueio (AD-4) e recuperação após rejeição.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferRejectionApiTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private JdbcTemplate jdbcTemplate;

    // --- limites de valor ---

    @Test
    void acceptsAmountEqualToSourceBalanceLeavingZero() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "100.00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("0.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("100.00");
    }

    @Test
    void acceptsSmallestPositiveAmount() {
        UUID sourceId = createAccount("1.00");
        UUID destinationId = createAccount("0.00");

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "0.01");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("0.99");
    }

    @Test
    void rejectsAnyAmountFromZeroBalanceAccount() {
        UUID sourceId = createAccount("0.00");
        UUID destinationId = createAccount("10.00");

        assertRejectedWithoutEffect(
                body(sourceId, destinationId, "0.01"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_FUNDS",
                sourceId,
                destinationId);
    }

    @Test
    void rejectsAmountWithMoreThanTwoDecimalPlacesWithoutRounding() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");

        // 10.005 arredondaria para 10.01 (HALF_UP) ou 10.00 (truncamento); nenhum dos dois é aceito.
        assertRejectedWithoutEffect(
                body(sourceId, destinationId, "10.005"),
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                sourceId,
                destinationId);
    }

    // --- campos ausentes e corpo malformado ---

    @Test
    void rejectsMissingAmount() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        Map<String, Object> requestBody = body(sourceId, destinationId, "10.00");
        requestBody.remove("amount");

        assertRejectedWithoutEffect(
                requestBody, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", sourceId, destinationId);
    }

    @Test
    void rejectsMissingSourceAccountId() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        Map<String, Object> requestBody = body(sourceId, destinationId, "10.00");
        requestBody.remove("sourceAccountId");

        assertRejectedWithoutEffect(
                requestBody, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", sourceId, destinationId);
    }

    @Test
    void rejectsMissingDestinationAccountId() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        Map<String, Object> requestBody = body(sourceId, destinationId, "10.00");
        requestBody.remove("destinationAccountId");

        assertRejectedWithoutEffect(
                requestBody, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", sourceId, destinationId);
    }

    @Test
    void rejectsNonUuidAccountId() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        Map<String, Object> requestBody = body(sourceId, destinationId, "10.00");
        requestBody.put("sourceAccountId", "not-a-uuid");

        assertRejectedWithoutEffect(
                requestBody, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", sourceId, destinationId);
    }

    @Test
    void rejectsMalformedJsonBody() {
        long transfersBefore = countTransfers();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/transfers",
                        HttpMethod.POST,
                        new HttpEntity<>("{\"amount\": ", headers),
                        JSON_OBJECT);

        assertProblem(response, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
    }

    // --- mais de uma violação: resposta determinística e sem efeito ---

    @Test
    void invalidInputTakesPrecedenceOverBusinessRules() {
        UUID accountId = createAccount("100.00");

        // Mesma conta e valor negativo: a entrada inválida (400) vence a regra de negócio (422).
        assertRejectedWithoutEffect(
                body(accountId, accountId, "-1.00"),
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                accountId,
                accountId);
    }

    @Test
    void sameAccountTakesPrecedenceOverUnknownAccount() {
        UUID unknownId = UUID.randomUUID();
        long accountsBefore = countAccounts();
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response = transfer(unknownId, unknownId, "10.00");

        assertProblem(response, HttpStatus.UNPROCESSABLE_ENTITY, "SAME_ACCOUNT");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(countAccounts()).isEqualTo(accountsBefore);
    }

    @Test
    void unknownSourceIsReportedFirstWhenBothAccountsAreUnknownRegardlessOfIdOrder() {
        UUID lower = new UUID(0L, 1L);
        UUID higher = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);

        assertProblem(transfer(lower, higher, "10.00"), HttpStatus.UNPROCESSABLE_ENTITY, "SOURCE_ACCOUNT_NOT_FOUND");
        assertProblem(transfer(higher, lower, "10.00"), HttpStatus.UNPROCESSABLE_ENTITY, "SOURCE_ACCOUNT_NOT_FOUND");
    }

    @Test
    void unknownDestinationTakesPrecedenceOverInsufficientFunds() {
        UUID sourceId = createAccount("10.00");

        assertRejectedWithoutEffect(
                body(sourceId, UUID.randomUUID(), "999.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "DESTINATION_ACCOUNT_NOT_FOUND",
                sourceId,
                sourceId);
    }

    @Test
    void unknownSourceTakesPrecedenceOverInsufficientFunds() {
        UUID destinationId = createAccount("10.00");

        assertRejectedWithoutEffect(
                body(UUID.randomUUID(), destinationId, "999.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "SOURCE_ACCOUNT_NOT_FOUND",
                destinationId,
                destinationId);
    }

    // --- ordem de bloqueio (AD-4): a regra vale para a origem, seja ela bloqueada 1ª ou 2ª ---

    @Test
    void insufficientFundsIsCheckedOnSourceWhateverItsLockPosition() {
        UUID a = createAccount("50.00");
        UUID b = createAccount("50.00");
        UUID lower = a.compareTo(b) < 0 ? a : b;
        UUID higher = lower.equals(a) ? b : a;
        // Destino com saldo alto: se a verificação olhasse a conta errada, a transferência passaria.
        jdbcTemplate.update("UPDATE account SET balance = 1000.00 WHERE id = ?", lower);

        assertRejectedWithoutEffect(
                body(higher, lower, "50.01"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_FUNDS",
                higher,
                lower);

        jdbcTemplate.update("UPDATE account SET balance = 50.00 WHERE id = ?", lower);
        jdbcTemplate.update("UPDATE account SET balance = 1000.00 WHERE id = ?", higher);

        assertRejectedWithoutEffect(
                body(lower, higher, "50.01"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_FUNDS",
                lower,
                higher);
    }

    @Test
    void unknownDestinationIsReportedWhateverItsLockPosition() {
        UUID sourceId = createAccount("100.00");
        // Extremos de UUID.compareTo (comparação com sinal): nenhum UUID v4 é igual a eles, porque
        // o nibble de versão (4) difere; o destino fica, com certeza, antes e depois da origem.
        UUID lowestDestination = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
        UUID highestDestination = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
        assertThat(lowestDestination.compareTo(sourceId)).isNegative();
        assertThat(highestDestination.compareTo(sourceId)).isPositive();

        assertRejectedWithoutEffect(
                body(sourceId, lowestDestination, "10.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "DESTINATION_ACCOUNT_NOT_FOUND",
                sourceId,
                sourceId);
        assertRejectedWithoutEffect(
                body(sourceId, highestDestination, "10.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "DESTINATION_ACCOUNT_NOT_FOUND",
                sourceId,
                sourceId);
    }

    // --- recuperação e contrato existente ---

    @Test
    void validTransferSucceedsAfterRejectionBetweenSameAccounts() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");

        assertProblem(
                transfer(sourceId, destinationId, "150.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_FUNDS");

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "60.00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("40.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("60.00");
        assertThat(transfersBetween(sourceId, destinationId)).isEqualTo(1);
    }

    @Test
    void unknownAccountInTransferDoesNotChangeAccountLookupContract() {
        UUID unknownId = UUID.randomUUID();
        UUID destinationId = createAccount("10.00");
        assertProblem(
                transfer(unknownId, destinationId, "1.00"),
                HttpStatus.UNPROCESSABLE_ENTITY,
                "SOURCE_ACCOUNT_NOT_FOUND");

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, unknownId);

        assertProblem(response, HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND");
    }

    // --- helpers ---

    private void assertRejectedWithoutEffect(
            Map<String, Object> requestBody,
            HttpStatus expectedStatus,
            String expectedCode,
            UUID firstAccountId,
            UUID secondAccountId) {
        BigDecimal firstBefore = balanceOf(firstAccountId);
        BigDecimal secondBefore = balanceOf(secondAccountId);
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response = post(requestBody);

        assertProblem(response, expectedStatus, expectedCode);
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(firstAccountId)).isEqualByComparingTo(firstBefore);
        assertThat(balanceOf(secondAccountId)).isEqualByComparingTo(secondBefore);
    }

    private void assertProblem(
            ResponseEntity<Map<String, Object>> response, HttpStatus expectedStatus, String expectedCode) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .isTrue();
        assertThat(response.getHeaders().getLocation()).isNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo(expectedCode);
        assertThat(response.getBody().get("status")).isEqualTo(expectedStatus.value());
    }

    private Map<String, Object> body(UUID sourceId, UUID destinationId, String amount) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sourceAccountId", sourceId);
        requestBody.put("destinationAccountId", destinationId);
        requestBody.put("amount", new BigDecimal(amount));
        return requestBody;
    }

    private ResponseEntity<Map<String, Object>> transfer(UUID sourceId, UUID destinationId, String amount) {
        return post(body(sourceId, destinationId, amount));
    }

    private ResponseEntity<Map<String, Object>> post(Map<String, Object> requestBody) {
        return restTemplate.exchange(
                "/transfers", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
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

    private long countTransfers() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM transfer", Long.class);
        return count == null ? 0 : count;
    }

    private long countAccounts() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account", Long.class);
        return count == null ? 0 : count;
    }

    private long transfersBetween(UUID sourceId, UUID destinationId) {
        Long count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM transfer WHERE source_account_id = ? AND destination_account_id = ?",
                        Long.class,
                        sourceId,
                        destinationId);
        return count == null ? 0 : count;
    }
}
