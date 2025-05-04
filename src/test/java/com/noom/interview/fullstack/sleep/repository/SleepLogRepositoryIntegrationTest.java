package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.domain.entity.*;
import com.noom.interview.fullstack.sleep.test.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class SleepLogRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SleepLogRepository sleepLogRepository;

    private UUID userId;
    private LocalDate today;
    private LocalDate yesterday;
    private LocalDate twoDaysAgo;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        sleepLogRepository.deleteAll();

        // Set up test data
        userId = UUID.randomUUID();
        today = LocalDate.now();
        yesterday = today.minusDays(1);
        twoDaysAgo = today.minusDays(2);

        // Create test sleep logs
        SleepLog sleepLogToday = createSleepLog(userId, today, Feeling.GOOD);
        SleepLog sleepLogYesterday = createSleepLog(userId, yesterday, Feeling.OK);
        SleepLog sleepLogTwoDaysAgo = createSleepLog(userId, twoDaysAgo, Feeling.BAD);

        // Save test data
        sleepLogRepository.saveAll(List.of(sleepLogToday, sleepLogYesterday, sleepLogTwoDaysAgo));
    }

    @Test
    void findFirstByUserIdOrderBySleepDateDesc_shouldReturnMostRecentSleepLog() {
        // When
        Optional<SleepLog> result = sleepLogRepository.findFirstByUserIdOrderBySleepDateDesc(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSleepDate()).isEqualTo(today);
        assertThat(result.get().getFeeling()).isEqualTo(Feeling.GOOD);
    }

    @Test
    void findFirstByUserIdOrderBySleepDateDesc_shouldReturnEmptyForNonExistentUser() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // When
        Optional<SleepLog> result = sleepLogRepository.findFirstByUserIdOrderBySleepDateDesc(nonExistentUserId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByUserIdAndSleepDateBetween_shouldReturnSleepLogsInDateRange() {
        // When
        List<SleepLog> result = sleepLogRepository.findByUserIdAndSleepDateBetween(userId, yesterday, today);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(SleepLog::getSleepDate)
                .containsExactlyInAnyOrder(yesterday, today);
    }

    @Test
    void findByUserIdAndSleepDateBetween_shouldReturnEmptyListForNonExistentUser() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // When
        List<SleepLog> result = sleepLogRepository.findByUserIdAndSleepDateBetween(nonExistentUserId, yesterday, today);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void existsByUserIdAndSleepDate_shouldReturnTrueForExistingSleepLog() {
        // When
        boolean exists = sleepLogRepository.existsByUserIdAndSleepDate(userId, today);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserIdAndSleepDate_shouldReturnFalseForNonExistentSleepLog() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();
        LocalDate futureDate = today.plusDays(1);

        // When
        boolean existsForNonExistentUser = sleepLogRepository.existsByUserIdAndSleepDate(nonExistentUserId, today);
        boolean existsForNonExistentDate = sleepLogRepository.existsByUserIdAndSleepDate(userId, futureDate);

        // Then
        assertThat(existsForNonExistentUser).isFalse();
        assertThat(existsForNonExistentDate).isFalse();
    }

    private SleepLog createSleepLog(UUID userId, LocalDate sleepDate, Feeling feeling) {
        Instant bedTime = sleepDate.atStartOfDay(ZoneId.systemDefault()).minus(8, ChronoUnit.HOURS).toInstant();
        Instant wakeTime = sleepDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        int totalTimeInBedMinutes = 8 * 60; // 8 hours in minutes

        return SleepLog.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(totalTimeInBedMinutes)
                .feeling(feeling)
                .build();
    }
}
