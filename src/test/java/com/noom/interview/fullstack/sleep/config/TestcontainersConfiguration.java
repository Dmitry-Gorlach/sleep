package com.noom.interview.fullstack.sleep.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private TestcontainersConfiguration() {
        // Private constructor to hide the implicit public one
    }

    @Bean
    @ServiceConnection
    public static PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:13-alpine"))
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres");
    }
}
