package com.noom.interview.fullstack.sleep;

import static com.noom.interview.fullstack.sleep.SleepApplication.UNIT_TEST_PROFILE;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(UNIT_TEST_PROFILE)
class SleepApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertThat(true).isTrue();
    }
}