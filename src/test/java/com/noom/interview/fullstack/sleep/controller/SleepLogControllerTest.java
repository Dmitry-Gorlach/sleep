package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SleepLogController.class)
@Import({com.noom.interview.fullstack.sleep.config.TestConfig.class,
        com.noom.interview.fullstack.sleep.exception.GlobalExceptionHandler.class})
@ActiveProfiles("unittest")
class SleepLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SleepLogService sleepLogService;

    private UUID userId;
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;
    private SleepLogRequest validRequest;
    private SleepLogResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Reset the mock before each test
        reset(sleepLogService);

        userId = UUID.randomUUID();
        sleepDate = LocalDate.now();
        bedTime = Instant.now().minus(8, ChronoUnit.HOURS);
        wakeTime = Instant.now();

        validRequest = SleepLogRequest.builder()
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .feeling(Feeling.GOOD)
                .build();

        expectedResponse = SleepLogResponse.builder()
                .sleepDate(sleepDate)
                .bedTime(bedTime)
                .wakeTime(wakeTime)
                .totalTimeInBedMinutes(480) // 8 hours in minutes
                .feeling(Feeling.GOOD)
                .build();
    }

    @Test
    void createSleepLog_ValidRequest_Returns201Created() throws Exception {
        // Arrange
        when(sleepLogService.createSleepLog(any(SleepLogRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/sleep-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", userId.toString())
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sleepDate").value(sleepDate.toString()))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(480))
                .andExpect(jsonPath("$.feeling").value(Feeling.GOOD.toString()));

        // Verify that the service was called with the correct request
        verify(sleepLogService).createSleepLog(any(SleepLogRequest.class));
    }

    @Test
    void createSleepLog_MissingUserId_Returns400BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/sleep-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(sleepLogService, never()).createSleepLog(any(SleepLogRequest.class));
    }

    @Test
    void createSleepLog_ServiceThrowsIllegalArgumentException_Returns400BadRequest() throws Exception {
        // Arrange
        when(sleepLogService.createSleepLog(any(SleepLogRequest.class)))
                .thenThrow(new IllegalArgumentException("Wake time must be after bed time"));

        // Act & Assert
        mockMvc.perform(post("/api/sleep-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", userId.toString())
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        // Verify that the service was called
        verify(sleepLogService).createSleepLog(any(SleepLogRequest.class));
    }

    @Test
    void createSleepLog_ServiceThrowsIllegalStateException_Returns409Conflict() throws Exception {
        // Arrange
        when(sleepLogService.createSleepLog(any(SleepLogRequest.class)))
                .thenThrow(new IllegalStateException("Sleep log already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/sleep-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-ID", userId.toString())
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());

        // Verify that the service was called
        verify(sleepLogService).createSleepLog(any(SleepLogRequest.class));
    }

    @Test
    void getLatestSleepLog_SleepLogExists_Returns200Ok() throws Exception {
        // Arrange
        when(sleepLogService.getLatestSleepLog(userId)).thenReturn(Optional.of(expectedResponse));

        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/latest")
                        .header("X-User-ID", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sleepDate").value(sleepDate.toString()))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(480))
                .andExpect(jsonPath("$.feeling").value(Feeling.GOOD.toString()));

        // Verify that the service was called with the correct userId
        verify(sleepLogService).getLatestSleepLog(userId);
    }

    @Test
    void getLatestSleepLog_NoSleepLogExists_Returns404NotFound() throws Exception {
        // Arrange
        String expectedErrorMessage = "No sleep logs found for user " + userId;
        when(sleepLogService.getLatestSleepLog(userId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/latest")
                        .header("X-User-ID", userId.toString()))
                .andExpect(status().isNotFound()) // Verify the HTTP status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verify content type
                // Verify the structure and content of the ErrorResponse body
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.path").value("/api/sleep-logs/latest"))
                .andExpect(jsonPath("$.timestamp").exists()) // Check timestamp exists
                // Optionally, check timestamp format or that it's recent (more complex assertion)
                .andExpect(jsonPath("$.timestamp", Matchers.matchesPattern(
                        // Regex for ISO 8601 OffsetDateTime like 2023-10-27T10:15:30.123456+01:00
                        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?([+\\-]\\d{2}:\\d{2}|Z)$"
                )));

        // Verify that the service was called with the correct userId
        verify(sleepLogService).getLatestSleepLog(userId);
    }

    @Test
    void getLatestSleepLog_MissingUserId_Returns400BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/latest"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(sleepLogService, never()).getLatestSleepLog(any());
    }

    @Test
    void getSleepStatistics_ValidRequest_Returns200Ok() throws Exception {
        // Arrange
        Map<Feeling, Integer> feelingCounts = new EnumMap<>(Feeling.class);
        feelingCounts.put(Feeling.GOOD, 3);
        feelingCounts.put(Feeling.OK, 2);
        feelingCounts.put(Feeling.BAD, 1);

        SleepStatisticsResponse statisticsResponse = SleepStatisticsResponse.builder()
                .averageTotalTimeInBedMinutes(480.0)
                .averageBedTime(LocalTime.of(22, 30))
                .averageWakeTime(LocalTime.of(6, 30))
                .feelingCounts(feelingCounts)
                .build();

        when(sleepLogService.getSleepStatistics(userId)).thenReturn(statisticsResponse);

        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/statistics")
                        .header("X-User-ID", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.averageTotalTimeInBedMinutes").value(480.0))
                .andExpect(jsonPath("$.averageBedTime").value("22:30:00"))
                .andExpect(jsonPath("$.averageWakeTime").value("06:30:00"))
                .andExpect(jsonPath("$.feelingCounts.GOOD").value(3))
                .andExpect(jsonPath("$.feelingCounts.OK").value(2))
                .andExpect(jsonPath("$.feelingCounts.BAD").value(1));

        // Verify that the service was called with the correct userId
        verify(sleepLogService).getSleepStatistics(userId);
    }

    @Test
    void getSleepStatistics_MissingUserId_Returns400BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/statistics"))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(sleepLogService, never()).getSleepStatistics(any());
    }

    @Test
    void getSleepStatistics_EmptyStatistics_Returns200OkWithEmptyData() throws Exception {
        // Arrange
        Map<Feeling, Integer> emptyFeelingCounts = new EnumMap<>(Feeling.class);
        emptyFeelingCounts.put(Feeling.GOOD, 0);
        emptyFeelingCounts.put(Feeling.OK, 0);
        emptyFeelingCounts.put(Feeling.BAD, 0);

        SleepStatisticsResponse emptyResponse = SleepStatisticsResponse.builder()
                .averageTotalTimeInBedMinutes(0.0)
                .averageBedTime(null)
                .averageWakeTime(null)
                .feelingCounts(emptyFeelingCounts)
                .build();

        when(sleepLogService.getSleepStatistics(userId)).thenReturn(emptyResponse);

        // Act & Assert
        mockMvc.perform(get("/api/sleep-logs/statistics")
                        .header("X-User-ID", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.averageTotalTimeInBedMinutes").value(0.0))
                .andExpect(jsonPath("$.averageBedTime").isEmpty())
                .andExpect(jsonPath("$.averageWakeTime").isEmpty())
                .andExpect(jsonPath("$.feelingCounts.GOOD").value(0))
                .andExpect(jsonPath("$.feelingCounts.OK").value(0))
                .andExpect(jsonPath("$.feelingCounts.BAD").value(0));

        // Verify that the service was called with the correct userId
        verify(sleepLogService).getSleepStatistics(userId);
    }
}
