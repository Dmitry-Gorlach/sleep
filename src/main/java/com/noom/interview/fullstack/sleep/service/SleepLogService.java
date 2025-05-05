package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.domain.dto.*;

import java.util.*;

/**
 * Service for managing sleep logs.
 */
public interface SleepLogService {

    /**
     * Creates a new sleep log.
     *
     * @param request the sleep log request
     * @return the created sleep log response
     * @throws IllegalArgumentException if the request is invalid (userId is null, wakeTime <= bedTime)
     * @throws IllegalStateException if a sleep log already exists for the given userId and sleepDate
     */
    SleepLogResponse createSleepLog(SleepLogRequest request);

    /**
     * Gets the latest sleep log for a user.
     *
     * @param userId the ID of the user
     * @return an Optional containing the latest sleep log, or empty if none exists
     */
    Optional<SleepLogResponse> getLatestSleepLog(UUID userId);

    /**
     * Gets sleep statistics for a user over the last 30 days.
     * 
     * @param userId the ID of the user
     * @return the sleep statistics response
     */
    SleepStatisticsResponse getSleepStatistics(UUID userId);
}
