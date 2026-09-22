package dev.agenticlab.reference;

import static org.assertj.core.api.Assertions.assertThat;

import dev.agenticlab.reference.support.PostgresContainerConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(PostgresContainerConfig.class)
class ApplicationSmokeTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Test
    void contextLoadsAgainstRealPostgres() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        assertThat(one).isEqualTo(1);
    }

    @Test
    void databaseIsPostgres17() {
        String version = jdbcTemplate.queryForObject("SHOW server_version", String.class);

        assertThat(version).startsWith("17");
    }

    @Test
    void flywayAppliesAllMigrationsSuccessfully() {
        MigrationInfo[] migrations = flyway.info().all();

        assertThat(migrations).isNotEmpty();
        assertThat(migrations)
                .allSatisfy(migration -> assertThat(migration.getState()).isEqualTo(MigrationState.SUCCESS));
    }
}
