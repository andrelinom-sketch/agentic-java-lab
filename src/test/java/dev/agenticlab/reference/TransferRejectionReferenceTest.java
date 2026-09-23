package dev.agenticlab.reference;

import static org.assertj.core.api.Assertions.assertThat;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Especificação executável da Story 1.3 / S3 (CC-EXP-02): um teste por
 * critério de aceite, cada um com uma única violação.
 *
 * <p>Estes testes fazem parte da fotografia inicial de {@code freeze/exp-02}
 * e nascem propositalmente vermelhos, porque as rejeições ainda não existem.
 * Todos verificam também o último critério de aceite: nenhuma Transferência
 * persistida, nenhum saldo alterado e resposta sem {@code id} e sem
 * {@code Location} (ver {@code docs/lab/02-claude-code-exp-02.md}).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferRejectionReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void rejectsUnknownSourceAccount() {
        UUID destinationId = createAccount("500.00");
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response =
                transfer(UUID.randomUUID(), destinationId, "100.00");

        assertRejected(response, HttpStatus.UNPROCESSABLE_ENTITY, "SOURCE_ACCOUNT_NOT_FOUND");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("500.00");
    }

    @Test
    void rejectsUnknownDestinationAccount() {
        UUID sourceId = createAccount("1000.00");
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response =
                transfer(sourceId, UUID.randomUUID(), "100.00");

        assertRejected(response, HttpStatus.UNPROCESSABLE_ENTITY, "DESTINATION_ACCOUNT_NOT_FOUND");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("1000.00");
    }

    @Test
    void rejectsZeroAmount() {
        assertInvalidAmountRejectedWithoutEffect("0.00");
    }

    @Test
    void rejectsNegativeAmount() {
        assertInvalidAmountRejectedWithoutEffect("-10.00");
    }

    @Test
    void rejectsAmountExceedingSourceBalance() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("500.00");
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "100.01");

        assertRejected(response, HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_FUNDS");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("100.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("500.00");
    }

    @Test
    void rejectsSameSourceAndDestination() {
        UUID accountId = createAccount("1000.00");
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response = transfer(accountId, accountId, "100.00");

        assertRejected(response, HttpStatus.UNPROCESSABLE_ENTITY, "SAME_ACCOUNT");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(accountId)).isEqualByComparingTo("1000.00");
    }

    private void assertInvalidAmountRejectedWithoutEffect(String amount) {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        long transfersBefore = countTransfers();

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, amount);

        assertRejected(response, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        assertThat(countTransfers()).isEqualTo(transfersBefore);
        assertThat(balanceOf(sourceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("500.00");
    }

    /** Problem Details (RFC 9457) com código estável, sem id e sem Location. */
    private void assertRejected(
            ResponseEntity<Map<String, Object>> response, HttpStatus expectedStatus, String expectedCode) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)).isTrue();
        assertThat(response.getHeaders().getLocation()).isNull();

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("code")).isEqualTo(expectedCode);
        assertThat(body).doesNotContainKey("id");
    }

    private UUID createAccount(String initialBalance) {
        Map<String, Object> requestBody = Map.of("initialBalance", new BigDecimal(initialBalance));
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
        return UUID.fromString(String.valueOf(response.getBody().get("id")));
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

    private BigDecimal balanceOf(UUID accountId) {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, accountId);
        return new BigDecimal(String.valueOf(response.getBody().get("balance")));
    }

    private long countTransfers() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM transfer", Long.class);
        return count == null ? 0 : count;
    }
}
