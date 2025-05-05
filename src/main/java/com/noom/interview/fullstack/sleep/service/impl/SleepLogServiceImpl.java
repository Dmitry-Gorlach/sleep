package com.noom.interview.fullstack.sleep.service.impl;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.SleepLog;
import com.noom.interview.fullstack.sleep.mapper.SleepLogMapper;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

/**
 * Implementation of the SleepLogService interface.
 */
@Service
@RequiredArgsConstructor
public class SleepLogServiceImpl implements SleepLogService {

    private final SleepLogRepository sleepLogRepository;
    private final SleepLogMapper sleepLogMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SleepLogResponse createSleepLog(SleepLogRequest request) {
        validateRequest(request);

        // Check if a sleep log already exists for the given userId and sleepDate
        if (sleepLogRepository.existsByUserIdAndSleepDate(request.getUserId(), request.getSleepDate())) {
            throw new IllegalStateException(
                    String.format("Sleep log already exists for user %s on date %s", 
                            request.getUserId(), request.getSleepDate()));
        }

        // Convert request to entity
        SleepLog sleepLog = sleepLogMapper.toEntity(request);

        // Calculate total time in bed in minutes
        long totalMinutes = calculateTotalTimeInBedMinutes(request.getBedTime(), request.getWakeTime());
        sleepLog.setTotalTimeInBedMinutes((int) totalMinutes);

        // Save the entity
        SleepLog savedSleepLog = sleepLogRepository.save(sleepLog);

        // Convert entity to response
        return sleepLogMapper.toResponse(savedSleepLog);
    }

    /**
     * Validates the sleep log request.
     *
     * @param request the sleep log request
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validateRequest(SleepLogRequest request) {
        // Validate userId
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Validate sleepDate
        if (request.getSleepDate() == null) {
            throw new IllegalArgumentException("Sleep date cannot be null");
        }

        // Validate bedTime
        if (request.getBedTime() == null) {
            throw new IllegalArgumentException("Bed time cannot be null");
        }

        // Validate wakeTime
        if (request.getWakeTime() == null) {
            throw new IllegalArgumentException("Wake time cannot be null");
        }

        // Validate wakeTime > bedTime
        if (!request.getWakeTime().isAfter(request.getBedTime())) {
            throw new IllegalArgumentException("Wake time must be after bed time");
        }

        // Validate feeling
        if (request.getFeeling() == null) {
            throw new IllegalArgumentException("Feeling cannot be null");
        }
    }

    /**
     * Calculates the total time in bed in minutes.
     *
     * @param bedTime the bedtime
     * @param wakeTime the wake time
     * @return the total time in bed in minutes
     */
    private long calculateTotalTimeInBedMinutes(java.time.Instant bedTime, java.time.Instant wakeTime) {
        return Duration.between(bedTime, wakeTime).toMinutes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SleepLogResponse> getLatestSleepLog(UUID userId) {
        return sleepLogRepository.findFirstByUserIdOrderBySleepDateDesc(userId)
                .map(sleepLogMapper::toResponse);
    }
}
