package com.noom.interview.fullstack.sleep.integration;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import com.noom.interview.fullstack.sleep.test.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepLogIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SleepLogService sleepLogService;

    @Autowired
    private SleepLogRepository sleepLogRepository;

    private UUID userId;
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        sleepLogRepository.deleteAll();

        // Set up test data
        userId = UUID.randomUUID();
        sleepDate = LocalDate.now();
        bedTime = Instant.now().minus(8, ChronoUnit.HOURS);
        wakeTime = Instant.now();
    }

    @Test
    void createSleepLog_ValidRequest_ReturnsResponse() {
        // Arrange
        SleepLogRequest request = SleepLogRequest.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .feeling(Feeling.GOOD)
                .build();

        // Act
        SleepLogResponse response = sleepLogService.createSleepLog(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSleepDate()).isEqualTo(sleepDate);
        assertThat(response.getBedTime()).isEqualTo(bedTime);
        assertThat(response.getWakeTime()).isEqualTo(wakeTime);
        assertThat(response.getFeeling()).isEqualTo(Feeling.GOOD);
        assertThat(response.getTotalTimeInBedMinutes()).isPositive();

        // Verify that the sleep log was saved to the database
        assertThat(sleepLogRepository.existsByUserIdAndSleepDate(userId, sleepDate)).isTrue();
    }

    @Test
    void createSleepLog_DuplicateSleepLog_ThrowsIllegalStateException() {
        // Arrange
        // First, create a sleep log
        SleepLog existingSleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480) // 8 hours in minutes
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(existingSleepLog);

        // Now try to create another sleep log for the same user and date
        SleepLogRequest request = SleepLogRequest.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .feeling(Feeling.BAD) // Different feeling
                .build();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> sleepLogService.createSleepLog(request));
        assertThat(exception.getMessage()).contains("Sleep log already exists");
    }

    @Test
    void createSleepLog_InvalidRequest_ThrowsIllegalArgumentException() {
        // Arrange - wakeTime before bedTime
        Instant invalidWakeTime = bedTime.minus(1, ChronoUnit.HOURS);
        SleepLogRequest request = SleepLogRequest.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(invalidWakeTime)
                .feeling(Feeling.GOOD)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sleepLogService.createSleepLog(request));
        assertThat(exception.getMessage()).contains("Wake time must be after bed time");
    }
}
