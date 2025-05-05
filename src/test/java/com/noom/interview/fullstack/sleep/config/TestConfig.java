package com.noom.interview.fullstack.sleep.config;

import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

/**
 * Test configuration for providing mock beans to the Spring context.
 * Only applied when the "unittest" profile is active.
 */
@Configuration
@Profile("unittest")
public class TestConfig {

    /**
     * Provides a mock SleepLogService bean for testing.
     *
     * @return a mock SleepLogService
     */
    @Bean
    @Primary
    public SleepLogService sleepLogService() {
        return Mockito.mock(SleepLogService.class);
    }
}
