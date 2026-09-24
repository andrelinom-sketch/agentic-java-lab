package dev.agenticlab.transfer;

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

/**
 * Testes do agente para a Story 1.5 (AD-8): HTTP contra PostgreSQL real. Cobrem casos além de
 * {@code TransferQueryReferenceTest}: o {@code Location} devolvido pelo POST, consulta repetida,
 * identificador malformado e ausência de efeito colateral no saldo.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferQueryApiTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void locationHeaderOfCreatedTransferPointsToTheQueryEndpoint() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");

        ResponseEntity<Map<String, Object>> created = transfer(sourceId, destinationId, "25.00");
        String location = created.getHeaders().getFirst("Location");
        assertThat(location).isNotNull();

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(location, HttpMethod.GET, null, JSON_OBJECT);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(String.valueOf(response.getBody().get("id")))
                .isEqualTo(String.valueOf(created.getBody().get("id")));
    }

    @Test
    void repeatedQueriesReturnTheSameRepresentation() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");
        UUID transferId = transferId(transfer(sourceId, destinationId, "10.00"));

        ResponseEntity<Map<String, Object>> first = getTransfer(transferId);
        ResponseEntity<Map<String, Object>> second = getTransfer(transferId);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getBody()).isEqualTo(first.getBody());
    }

    @Test
    void queryDoesNotChangeBalances() {
        UUID sourceId = createAccount("100.00");
        UUID destinationId = createAccount("0.00");
        UUID transferId = transferId(transfer(sourceId, destinationId, "40.00"));

        getTransfer(transferId);
        getTransfer(transferId);

        assertThat(balanceOf(sourceId)).isEqualByComparingTo("60.00");
        assertThat(balanceOf(destinationId)).isEqualByComparingTo("40.00");
    }

    @Test
    void eachOfSeveralTransfersIsQueriedIndividually() {
        UUID accountA = createAccount("100.00");
        UUID accountB = createAccount("0.00");
        UUID firstId = transferId(transfer(accountA, accountB, "10.00"));
        UUID secondId = transferId(transfer(accountA, accountB, "20.00"));

        assertThat(new BigDecimal(String.valueOf(getTransfer(firstId).getBody().get("amount"))))
                .isEqualByComparingTo("10.00");
        assertThat(new BigDecimal(String.valueOf(getTransfer(secondId).getBody().get("amount"))))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void rejectsMalformedTransferIdentifier() {
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange("/transfers/not-a-uuid", HttpMethod.GET, null, JSON_OBJECT);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("code")).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void accountIdentifierIsNotAValidTransferIdentifier() {
        UUID accountId = createAccount("100.00");

        ResponseEntity<Map<String, Object>> response = getTransfer(accountId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)).isTrue();
        assertThat(response.getBody().get("code")).isEqualTo("TRANSFER_NOT_FOUND");
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

    private ResponseEntity<Map<String, Object>> getTransfer(UUID transferId) {
        return restTemplate.exchange("/transfers/{id}", HttpMethod.GET, null, JSON_OBJECT, transferId);
    }
}
