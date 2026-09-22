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
import org.springframework.http.ResponseEntity;

/**
 * Especificação executável da Story 1.2 / S2 (Experimento 01).
 *
 * <p>Estes testes fazem parte da fotografia inicial de {@code freeze/exp-01}:
 * nascem propositalmente vermelhos, porque {@code POST /transfers} ainda não
 * existe. O objetivo do Experimento 01 é fazê-los ficar verdes sem alterar a
 * baseline congelada (ver {@code docs/lab/baseline.md} e {@code CLAUDE.md}).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferApiReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void transferBetweenExistingAccountsDebitsAndCredits() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "100.00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(UUID.fromString(String.valueOf(body.get("id")))).isNotNull();
        assertThat(String.valueOf(body.get("sourceAccountId"))).isEqualTo(sourceId.toString());
        assertThat(String.valueOf(body.get("destinationAccountId"))).isEqualTo(destinationId.toString());
        assertThat(new BigDecimal(String.valueOf(body.get("amount")))).isEqualByComparingTo("100.00");
        assertThat(body.get("status")).isEqualTo("COMPLETED");
        assertThat(body.get("createdAt")).isNotNull();

        assertThat(balanceOf(sourceId)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("600.00");
    }

    @Test
    void transferPreservesSumOfBalances() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("500.00");
        BigDecimal sumBefore = balanceOf(sourceId).add(balanceOf(destinationId));

        ResponseEntity<Map<String, Object>> response = transfer(sourceId, destinationId, "250.00");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BigDecimal sumAfter = balanceOf(sourceId).add(balanceOf(destinationId));
        assertThat(sumAfter).isEqualByComparingTo(sumBefore);
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
}
