package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import com.noom.interview.fullstack.sleep.mapper.SleepLogMapper;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.service.impl.SleepLogServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SleepLogServiceTest {

    @Mock
    private SleepLogRepository sleepLogRepository;

    @Mock
    private SleepLogMapper sleepLogMapper;

    @InjectMocks
    private SleepLogServiceImpl sleepLogService;

    private UUID userId;
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;
    private SleepLogRequest validRequest;
    private SleepLog sleepLog;
    private SleepLogResponse expectedResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sleepDate = LocalDate.now();
        bedTime = Instant.now().minus(8, ChronoUnit.HOURS);
        wakeTime = Instant.now();

        validRequest = SleepLogRequest.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .feeling(Feeling.GOOD)
                .build();

        sleepLog = SleepLog.builder()
                .id(1L)
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480) // 8 hours in minutes
                .feeling(Feeling.GOOD)
                .build();

        expectedResponse = SleepLogResponse.builder()
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();
    }

    @Test
    void createSleepLog_ValidRequest_ReturnsSleepLogResponse() {
        // Arrange
        when(sleepLogRepository.existsByUserIdAndSleepDate(userId, sleepDate)).thenReturn(false);
        when(sleepLogMapper.toEntity(validRequest)).thenReturn(sleepLog);
        when(sleepLogRepository.save(any(SleepLog.class))).thenReturn(sleepLog);
        when(sleepLogMapper.toResponse(sleepLog)).thenReturn(expectedResponse);

        // Act
        SleepLogResponse response = sleepLogService.createSleepLog(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(sleepLogRepository).existsByUserIdAndSleepDate(userId, sleepDate);
        verify(sleepLogMapper).toEntity(validRequest);
        verify(sleepLogRepository).save(any(SleepLog.class));
        verify(sleepLogMapper).toResponse(sleepLog);
    }

    @Test
    void createSleepLog_NullUserId_ThrowsIllegalArgumentException() {
        // Arrange
        SleepLogRequest invalidRequest = SleepLogRequest.builder()
                .userId(null)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .feeling(Feeling.GOOD)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sleepLogService.createSleepLog(invalidRequest));
        assertEquals("User ID cannot be null", exception.getMessage());
        verify(sleepLogRepository, never()).existsByUserIdAndSleepDate(any(), any());
        verify(sleepLogMapper, never()).toEntity(any());
        verify(sleepLogRepository, never()).save(any());
    }

    @Test
    void createSleepLog_WakeTimeBeforeBedTime_ThrowsIllegalArgumentException() {
        // Arrange
        Instant invalidWakeTime = bedTime.minus(1, ChronoUnit.HOURS);
        SleepLogRequest invalidRequest = SleepLogRequest.builder()
                .userId(userId)
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(invalidWakeTime)
                .feeling(Feeling.GOOD)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sleepLogService.createSleepLog(invalidRequest));
        assertEquals("Wake time must be after bed time", exception.getMessage());
        verify(sleepLogRepository, never()).existsByUserIdAndSleepDate(any(), any());
        verify(sleepLogMapper, never()).toEntity(any());
        verify(sleepLogRepository, never()).save(any());
    }

    @Test
    void createSleepLog_ExistingSleepLog_ThrowsIllegalStateException() {
        // Arrange
        when(sleepLogRepository.existsByUserIdAndSleepDate(userId, sleepDate)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> sleepLogService.createSleepLog(validRequest));
        assertTrue(exception.getMessage().contains("Sleep log already exists"));
        verify(sleepLogRepository).existsByUserIdAndSleepDate(userId, sleepDate);
        verify(sleepLogMapper, never()).toEntity(any());
        verify(sleepLogRepository, never()).save(any());
    }
}