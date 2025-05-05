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
import java.util.*;

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

    @Test
    void getLatestSleepLog_NoSleepLogs_ReturnsEmptyOptional() {
        // Act
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestSleepLog_OneSleepLog_ReturnsThatSleepLog() {
        // Arrange
        SleepLog sleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480) // 8 hours in minutes
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(sleepLog);

        // Act
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert
        assertThat(result).isPresent();
        SleepLogResponse response = result.get();
        assertThat(response.getSleepDate()).isEqualTo(sleepDate);
        assertThat(response.getBedTime()).isEqualTo(bedTime);
        assertThat(response.getWakeTime()).isEqualTo(wakeTime);
        assertThat(response.getFeeling()).isEqualTo(Feeling.GOOD);
        assertThat(response.getTotalTimeInBedMinutes()).isEqualTo(480);
    }

    @Test
    void getLatestSleepLog_MultipleSleepLogs_ReturnsLatestSleepLog() {
        // Arrange
        LocalDate olderDate = sleepDate.minusDays(1);
        LocalDate newerDate = sleepDate.plusDays(1);

        // Create older sleep log
        SleepLog olderSleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(olderDate)
                .bedTime(bedTime.minus(24, ChronoUnit.HOURS))
                .wakeTime(wakeTime.minus(24, ChronoUnit.HOURS))
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.BAD)
                .build();
        sleepLogRepository.save(olderSleepLog);

        // Create middle sleep log (current date)
        SleepLog middleSleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(middleSleepLog);

        // Create newer sleep log
        SleepLog newerSleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(newerDate)
                .bedTime(bedTime.plus(24, ChronoUnit.HOURS))
                .wakeTime(wakeTime.plus(24, ChronoUnit.HOURS))
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(newerSleepLog);

        // Act
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert
        assertThat(result).isPresent();
        SleepLogResponse response = result.get();
        assertThat(response.getSleepDate()).isEqualTo(newerDate);
        assertThat(response.getFeeling()).isEqualTo(Feeling.GOOD);
    }

    @Test
    void getLatestSleepLog_DifferentUsers_ReturnsCorrectUserSleepLog() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();

        // Create sleep log for main user
        SleepLog userSleepLog = SleepLog.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(userSleepLog);

        // Create sleep log for other user with newer date
        SleepLog otherUserSleepLog = SleepLog.builder()
                .userId(otherUserId)
                .sleepDate(sleepDate.plusDays(5))
                .bedTime(bedTime.plus(5, ChronoUnit.DAYS))
                .wakeTime(wakeTime.plus(5, ChronoUnit.DAYS))
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();
        sleepLogRepository.save(otherUserSleepLog);

        // Act - Get latest sleep log for main user
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert - Should return the main user's sleep log
        assertThat(result).isPresent();
        SleepLogResponse response = result.get();
        assertThat(response.getSleepDate()).isEqualTo(sleepDate);
        assertThat(response.getFeeling()).isEqualTo(Feeling.GOOD);

        // Act - Get latest sleep log for other user
        Optional<SleepLogResponse> otherResult = sleepLogService.getLatestSleepLog(otherUserId);

        // Assert - Should return the other user's sleep log
        assertThat(otherResult).isPresent();
        SleepLogResponse otherResponse = otherResult.get();
        assertThat(otherResponse.getSleepDate()).isEqualTo(sleepDate.plusDays(5));
        assertThat(otherResponse.getFeeling()).isEqualTo(Feeling.GOOD);
    }
}
