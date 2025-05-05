package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SleepLogMapperTest {

    @Autowired
    private SleepLogMapper sleepLogMapper;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate SLEEP_DATE = LocalDate.of(2023, 5, 15);
    private static final Instant BED_TIME = Instant.parse("2023-05-15T22:00:00Z");
    private static final Instant WAKE_TIME = Instant.parse("2023-05-16T06:00:00Z");
    private static final Integer TOTAL_TIME_IN_BED_MINUTES = 480; // 8 hours
    private static final Feeling FEELING = Feeling.GOOD;

    @Test
    void toEntity_shouldMapRequestToEntity() {
        // Arrange
        SleepLogRequest request = SleepLogRequest.builder()
                .userId(USER_ID)
                .sleepDate(SLEEP_DATE)
                .bedTime(BED_TIME)
                .wakeTime(WAKE_TIME)
                .feeling(FEELING)
                .build();

        // Act
        SleepLog entity = sleepLogMapper.toEntity(request);

        // Assert
        assertEquals(USER_ID, entity.getUserId());
        assertEquals(SLEEP_DATE, entity.getSleepDate());
        assertEquals(BED_TIME, entity.getBedTime());
        assertEquals(WAKE_TIME, entity.getWakeTime());
        assertEquals(FEELING, entity.getFeeling());

        // Fields that should be ignored
        assertNull(entity.getId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getTotalTimeInBedMinutes());
    }

    @Test
    void toResponse_shouldMapEntityToResponse() {
        // Arrange
        SleepLog entity = SleepLog.builder()
                .id(1L)
                .userId(USER_ID)
                .sleepDate(SLEEP_DATE)
                .bedTime(BED_TIME)
                .wakeTime(WAKE_TIME)
                .totalTimeInBedMinutes(TOTAL_TIME_IN_BED_MINUTES)
                .feeling(FEELING)
                .createdAt(Instant.now())
                .build();

        // Act
        SleepLogResponse response = sleepLogMapper.toResponse(entity);

        // Assert
        assertEquals(SLEEP_DATE, response.getSleepDate());
        assertEquals(BED_TIME, response.getBedTime());
        assertEquals(WAKE_TIME, response.getWakeTime());
        assertEquals(TOTAL_TIME_IN_BED_MINUTES, response.getTotalTimeInBedMinutes());
        assertEquals(FEELING, response.getFeeling());
    }
}
