package dev.agenticlab.reference.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * PostgreSQL real para os testes, compartilhado pelo cache de contexto do Spring.
 * A imagem deve ser a mesma do docker-compose.yml.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresContainerConfig {

    static final String IMAGE = "postgres:17";

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>(DockerImageName.parse(IMAGE));
    }
}
