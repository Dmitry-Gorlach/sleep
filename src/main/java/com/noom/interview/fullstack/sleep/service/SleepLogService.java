package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.domain.dto.*;

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
}