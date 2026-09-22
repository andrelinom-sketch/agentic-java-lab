package dev.agenticlab.reference;

import static org.assertj.core.api.Assertions.assertThat;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
import java.net.URI;
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
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testes de referência da Story 1.1 (AD-8): HTTP contra PostgreSQL real via
 * Testcontainers, um teste por critério de aceite.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class AccountApiReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void createsAccountAndReturnsLocationAndBody() {
        ResponseEntity<Map<String, Object>> response = createAccount("100.00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(UUID.fromString(String.valueOf(body.get("id")))).isNotNull();
        assertThat(new BigDecimal(String.valueOf(body.get("balance")))).isEqualByComparingTo("100.00");
    }

    @Test
    void getsCreatedAccountByLocation() {
        URI location = createAccount("250.50").getHeaders().getLocation();

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(location, HttpMethod.GET, null, JSON_OBJECT);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new BigDecimal(String.valueOf(response.getBody().get("balance"))))
                .isEqualByComparingTo("250.50");
    }

    @Test
    void rejectsNegativeInitialBalanceWithoutCreatingAccount() {
        long countBefore = countAccounts();

        ResponseEntity<Map<String, Object>> response = createAccount("-1.00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("code")).isEqualTo("VALIDATION_ERROR");
        assertThat(countAccounts()).isEqualTo(countBefore);
    }

    @Test
    void rejectsInitialBalanceWithMoreThanTwoDecimalPlacesWithoutRounding() {
        ResponseEntity<Map<String, Object>> response = createAccount("100.123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("code")).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void returnsNotFoundForUnknownAccountId() {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "/accounts/{id}", HttpMethod.GET, null, JSON_OBJECT, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("code")).isEqualTo("ACCOUNT_NOT_FOUND");
    }

    private ResponseEntity<Map<String, Object>> createAccount(String initialBalance) {
        Map<String, Object> requestBody = Map.of("initialBalance", new BigDecimal(initialBalance));
        return restTemplate.exchange(
                "/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), JSON_OBJECT);
    }

    private long countAccounts() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account", Long.class);
        return count == null ? 0 : count;
    }
}
