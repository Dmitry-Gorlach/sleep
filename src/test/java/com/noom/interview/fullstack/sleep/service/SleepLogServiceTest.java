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
import java.util.*;

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

    @Test
    void getLatestSleepLog_SleepLogExists_ReturnsOptionalWithSleepLogResponse() {
        // Arrange
        when(sleepLogRepository.findFirstByUserIdOrderBySleepDateDesc(userId)).thenReturn(Optional.of(sleepLog));
        when(sleepLogMapper.toResponse(sleepLog)).thenReturn(expectedResponse);

        // Act
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(sleepLogRepository).findFirstByUserIdOrderBySleepDateDesc(userId);
        verify(sleepLogMapper).toResponse(sleepLog);
    }

    @Test
    void getLatestSleepLog_NoSleepLogExists_ReturnsEmptyOptional() {
        // Arrange
        when(sleepLogRepository.findFirstByUserIdOrderBySleepDateDesc(userId)).thenReturn(Optional.empty());

        // Act
        Optional<SleepLogResponse> result = sleepLogService.getLatestSleepLog(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(sleepLogRepository).findFirstByUserIdOrderBySleepDateDesc(userId);
        verify(sleepLogMapper, never()).toResponse(any());
    }

    @Test
    void getSleepStatistics_WithSleepLogs_ReturnsCorrectStatistics() {
        // Arrange
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 30 days including today

        // Create test sleep logs
        List<SleepLog> sleepLogs = createTestSleepLogs();

        // Mock repository to return test sleep logs
        when(sleepLogRepository.findByUserIdAndSleepDateBetween(userId, startDate, endDate))
                .thenReturn(sleepLogs);

        // Act
        SleepStatisticsResponse response = sleepLogService.getSleepStatistics(userId);

        // Assert
        assertNotNull(response);
        assertEquals(480.0, response.getAverageTotalTimeInBedMinutes());
        assertNotNull(response.getAverageBedTime());
        assertNotNull(response.getAverageWakeTime());
        assertNotNull(response.getFeelingCounts());

        // Verify feeling counts
        Map<Feeling, Integer> feelingCounts = response.getFeelingCounts();
        assertEquals(1, feelingCounts.get(Feeling.GOOD));
        assertEquals(1, feelingCounts.get(Feeling.OK));
        assertEquals(1, feelingCounts.get(Feeling.BAD));

        // Verify repository was called with correct parameters
        verify(sleepLogRepository).findByUserIdAndSleepDateBetween(userId, startDate, endDate);
    }

    @Test
    void getSleepStatistics_NoSleepLogs_ReturnsEmptyStatistics() {
        // Arrange
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 30 days including today

        // Mock repository to return empty list
        when(sleepLogRepository.findByUserIdAndSleepDateBetween(userId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        // Act
        SleepStatisticsResponse response = sleepLogService.getSleepStatistics(userId);

        // Assert
        assertNotNull(response);
        assertEquals(0.0, response.getAverageTotalTimeInBedMinutes());
        assertNull(response.getAverageBedTime());
        assertNull(response.getAverageWakeTime());

        // Verify all feelings have zero count
        Map<Feeling, Integer> feelingCounts = response.getFeelingCounts();
        assertNotNull(feelingCounts);
        assertEquals(0, feelingCounts.get(Feeling.GOOD));
        assertEquals(0, feelingCounts.get(Feeling.OK));
        assertEquals(0, feelingCounts.get(Feeling.BAD));

        // Verify repository was called with correct parameters
        verify(sleepLogRepository).findByUserIdAndSleepDateBetween(userId, startDate, endDate);
    }

    /**
     * Helper method to create test sleep logs with different feelings
     */
    private List<SleepLog> createTestSleepLogs() {
        LocalDate today = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault();

        // Create three sleep logs with different feelings
        SleepLog log1 = SleepLog.builder()
                .id(1L)
                .userId(userId)
                .sleepDate(today.minusDays(1))
                .bedTime(LocalDateTime.of(today.minusDays(1), LocalTime.of(22, 0))
                        .atZone(zoneId).toInstant())
                .wakeTime(LocalDateTime.of(today, LocalTime.of(6, 0))
                        .atZone(zoneId).toInstant())
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.GOOD)
                .build();

        SleepLog log2 = SleepLog.builder()
                .id(2L)
                .userId(userId)
                .sleepDate(today.minusDays(2))
                .bedTime(LocalDateTime.of(today.minusDays(2), LocalTime.of(22, 0))
                        .atZone(zoneId).toInstant())
                .wakeTime(LocalDateTime.of(today.minusDays(1), LocalTime.of(6, 0))
                        .atZone(zoneId).toInstant())
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.OK)
                .build();

        SleepLog log3 = SleepLog.builder()
                .id(3L)
                .userId(userId)
                .sleepDate(today.minusDays(3))
                .bedTime(LocalDateTime.of(today.minusDays(3), LocalTime.of(22, 0))
                        .atZone(zoneId).toInstant())
                .wakeTime(LocalDateTime.of(today.minusDays(2), LocalTime.of(6, 0))
                        .atZone(zoneId).toInstant())
                .totalTimeInBedMinutes(480)
                .feeling(Feeling.BAD)
                .build();

        return Arrays.asList(log1, log2, log3);
    }
}
