package com.noom.interview.fullstack.sleep.test;

import com.noom.interview.fullstack.sleep.config.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Abstract base class for integration tests.
 * This class configures tests to use PostgreSQL via Testcontainers.
 */
@SpringBootTest
@ActiveProfiles("integration")
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {
    // Using Testcontainers configuration
}
