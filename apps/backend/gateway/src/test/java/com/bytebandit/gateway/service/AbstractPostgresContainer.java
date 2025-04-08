package com.bytebandit.gateway.service;

import io.github.cdimascio.dotenv.Dotenv;
import java.time.Duration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractPostgresContainer {

    static final Dotenv dotenv = Dotenv.configure()
        .filename(".env") // optional, defaults to ".env"
        .load();

    @Container
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
        "postgres:17-alpine")
        .withDatabaseName("testdb")
        .withUsername("postgres")
        .withPassword(dotenv.get("POSTGRES_PASSWORD"))
        .withReuse(true)
        .withStartupTimeout(Duration.ofMinutes(3));

    /**
     * This method sets up the PostgreSQL container properties for the Spring application context.
     * It registers the container's JDBC URL, username, password, and driver class name as dynamic
     * properties.
     *
     * @param registry The registry to which the dynamic properties are added.
     */
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name",
            POSTGRESQL_CONTAINER::getDriverClassName
        );
    }
}
