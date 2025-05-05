package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SleepStatisticsMapperTest {

    @Autowired
    private SleepStatisticsMapper sleepStatisticsMapper;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.of(2023, 5, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 5, 30);
    private static final String AVERAGE_BED_TIME = "22:30:00";
    private static final String AVERAGE_WAKE_TIME = "06:30:00";

    @Test
    void toResponse_shouldMapToStatisticsResponse() {
        // Arrange
        List<SleepLog> sleepLogs = Arrays.asList(
                createSleepLog(1L, LocalDate.of(2023, 5, 10), 480),
                createSleepLog(2L, LocalDate.of(2023, 5, 11), 420),
                createSleepLog(3L, LocalDate.of(2023, 5, 12), 510)
        );

        DateRange dateRange = new DateRange(START_DATE, END_DATE);

        Map<Feeling, Integer> feelingFrequencies = new EnumMap<>(Feeling.class);
        feelingFrequencies.put(Feeling.GOOD, 2);
        feelingFrequencies.put(Feeling.OK, 1);

        // Act
        SleepStatisticsResponse response = sleepStatisticsMapper.toResponse(
                sleepLogs, dateRange, AVERAGE_BED_TIME, AVERAGE_WAKE_TIME, feelingFrequencies);

        // Assert
        assertEquals(dateRange, response.getDateRange());
        assertEquals(470, response.getAverageTotalTimeInBedMinutes()); // (480 + 420 + 510) / 3 = 470
        assertEquals(AVERAGE_BED_TIME, response.getAverageBedTime());
        assertEquals(AVERAGE_WAKE_TIME, response.getAverageWakeTime());
        assertEquals(feelingFrequencies, response.getFeelingFrequencies());
    }

    @Test
    void calculateAverageTotalTimeInBedMinutes_shouldReturnZeroForEmptyList() {
        // Act
        Integer result = sleepStatisticsMapper.calculateAverageTotalTimeInBedMinutes(Collections.emptyList());

        // Assert
        assertEquals(0, result);
    }

    @Test
    void calculateAverageTotalTimeInBedMinutes_shouldReturnZeroForNullList() {
        // Act
        Integer result = sleepStatisticsMapper.calculateAverageTotalTimeInBedMinutes(null);

        // Assert
        assertEquals(0, result);
    }

    @Test
    void calculateAverageTotalTimeInBedMinutes_shouldCalculateAverage() {
        // Arrange
        List<SleepLog> sleepLogs = Arrays.asList(
                createSleepLog(1L, LocalDate.of(2023, 5, 10), 480),
                createSleepLog(2L, LocalDate.of(2023, 5, 11), 420),
                createSleepLog(3L, LocalDate.of(2023, 5, 12), 510)
        );

        // Act
        Integer result = sleepStatisticsMapper.calculateAverageTotalTimeInBedMinutes(sleepLogs);

        // Assert
        assertEquals(470, result); // (480 + 420 + 510) / 3 = 470
    }

    private SleepLog createSleepLog(Long id, LocalDate sleepDate, Integer totalTimeInBedMinutes) {
        return SleepLog.builder()
                .id(id)
                .userId(USER_ID)
                .sleepDate(sleepDate)
                .bedTime(Instant.parse("2023-05-15T22:00:00Z"))
                .wakeTime(Instant.parse("2023-05-16T06:00:00Z"))
                .totalTimeInBedMinutes(totalTimeInBedMinutes)
                .feeling(Feeling.GOOD)
                .createdAt(Instant.now())
                .build();
    }
}
