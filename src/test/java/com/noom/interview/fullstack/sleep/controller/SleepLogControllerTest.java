package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SleepLogController.class)
class SleepLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SleepLogService sleepLogService;

    private UUID userId;
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;
    private SleepLogRequest validRequest;
    private SleepLogResponse expectedResponse;

    @BeforeEach
    void setUp() {
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
}