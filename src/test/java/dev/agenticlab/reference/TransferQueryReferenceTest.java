package dev.agenticlab.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Especificação executável da Story 1.5 / S5 (DEVIN-EXP-01): um teste por
 * critério de aceite, com preparação só pela API pública.
 *
 * <p>Na fotografia inicial do freeze, os testes de consulta nascem vermelhos,
 * porque {@code GET /transfers/{id}} ainda não existe. O 404 que o baseline
 * devolve para uma rota inexistente não satisfaz o critério de "não
 * encontrado": ele exige Problem Details com o código estável
 * {@code TRANSFER_NOT_FOUND} (AD-7). O teste de ids distintos já nasce verde.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostgresContainerConfig.class)
class TransferQueryReferenceTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {};

    @Autowired private TestRestTemplate restTemplate;

    /** AC1: 200 com a Transferência consultada e o contrato completo do AD-7. */
    @Test
    void getsExistingTransferById() {
        UUID accountA = createAccount("1000.00");
        UUID accountB = createAccount("500.00");
        UUID accountC = createAccount("0.00");

        ResponseEntity<Map<String, Object>> created = transfer(accountA, accountB, "100.00");
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> createdBody = created.getBody();
        assertThat(createdBody).isNotNull();
        UUID transferId = UUID.fromString(String.valueOf(createdBody.get("id")));
        Instant createdAt = Instant.parse(String.valueOf(createdBody.get("createdAt")));

        // Uma segunda Transferência, posterior e diferente, para que a consulta não
        // possa devolver simplesmente a mais recente.
        assertThat(transfer(accountB, accountC, "30.00").getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map<String, Object>> response = getTransfer(transferId);

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
        assertThat(body.get("status")).isEqualTo("COMPLETED");
        // O banco guarda microssegundos; o POST devolve o instante em memória.
        assertThat(Instant.parse(String.valueOf(body.get("createdAt"))))
                .isCloseTo(createdAt, within(1, ChronoUnit.MILLIS));
    }

    /** AC2: id inexistente responde 404 em Problem Details com código estável. */
    @Test
    void returnsNotFoundProblemForUnknownTransfer() {
        ResponseEntity<Map<String, Object>> response = getTransfer(UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)).isTrue();

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(404);
        assertThat(body.get("code")).isEqualTo("TRANSFER_NOT_FOUND");
    }

    /**
     * AC3: duas Transferências efetivadas têm ids diferentes, mesmo com pedidos
     * idênticos (o id não deriva do conteúdo; FR-17 não implica idempotência).
     */
    @Test
    void distinctTransfersHaveDistinctIds() {
        UUID sourceId = createAccount("1000.00");
        UUID destinationId = createAccount("0.00");

        ResponseEntity<Map<String, Object>> first = transfer(sourceId, destinationId, "10.00");
        ResponseEntity<Map<String, Object>> second = transfer(sourceId, destinationId, "10.00");

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(first.getBody()).isNotNull();
        assertThat(second.getBody()).isNotNull();
        UUID firstId = UUID.fromString(String.valueOf(first.getBody().get("id")));
        UUID secondId = UUID.fromString(String.valueOf(second.getBody().get("id")));
        assertThat(firstId).isNotEqualTo(secondId);
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

    private ResponseEntity<Map<String, Object>> getTransfer(UUID transferId) {
        return restTemplate.exchange(
                "/transfers/{id}", HttpMethod.GET, null, JSON_OBJECT, transferId);
    }
}
