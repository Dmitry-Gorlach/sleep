package com.noom.interview.fullstack.sleep.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Abstract base class for integration tests.
 * This class configures tests to use H2 in-memory database instead of PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("integration")
public abstract class AbstractIntegrationTest {
    // No Testcontainers configuration
}