package com.noom.interview.fullstack.sleep.service.impl;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import com.noom.interview.fullstack.sleep.mapper.SleepLogMapper;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the SleepLogService interface.
 */
@Service
@RequiredArgsConstructor
public class SleepLogServiceImpl implements SleepLogService {

    private static final int DAYS_IN_STATISTICS_RANGE = 29;  //30 days including today

    private final SleepLogRepository sleepLogRepository;
    private final SleepLogMapper sleepLogMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SleepLogResponse createSleepLog(SleepLogRequest request) {
        validateRequest(request);

        if (sleepLogRepository.existsByUserIdAndSleepDate(request.getUserId(), request.getSleepDate())) {
            throw new IllegalStateException(
                    String.format("Sleep log already exists for user %s on date %s", 
                            request.getUserId(), request.getSleepDate()));
        }

        SleepLog sleepLog = sleepLogMapper.toEntity(request);

        long totalMinutes = calculateTotalTimeInBedMinutes(request.getBedTime(), request.getWakeTime());
        sleepLog.setTotalTimeInBedMinutes((int) totalMinutes);

        SleepLog savedSleepLog = sleepLogRepository.save(sleepLog);
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public SleepStatisticsResponse getSleepStatistics(UUID userId) {
        DateRange dateRange = calculateDateRange();
        List<SleepLog> sleepLogs = retrieveSleepLogs(userId, dateRange);

        if (sleepLogs.isEmpty()) {
           return createEmptyStatisticsResponse(dateRange);
        }

        double averageSleepDurationMinutes = calculateAverageSleepDuration(sleepLogs);
        AverageSleepTimes averageTimes = calculateAverageBedAndWakeTimes(sleepLogs);
        Map<Feeling, Integer> feelingCounts = countFeelings(sleepLogs);

        return buildStatisticsResponse(
                dateRange,
                averageSleepDurationMinutes,
                averageTimes.getBedTime(),
                averageTimes.getWakeTime(),
                feelingCounts
        );
    }

    /**
     * Calculates the date range for sleep statistics (30 days from now).
     *
     * @return a DateRange object containing start and end dates
     */
    private DateRange calculateDateRange() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(DAYS_IN_STATISTICS_RANGE);

        return new DateRange(startDate, endDate);
    }

    /**
     * Retrieves sleep logs for the user within the specified date range.
     *
     * @param userId the user ID
     * @param dateRange the date range
     * @return a list of sleep logs
     */
    private List<SleepLog> retrieveSleepLogs(UUID userId, DateRange dateRange) {
        return sleepLogRepository.findByUserIdAndSleepDateBetween(userId, dateRange.getFrom(), dateRange.getTo());
    }

    /**
     * Creates an empty statistics response when no sleep logs are found.
     *
     * @param dateRange the date range
     * @return an empty statistics response
     */
    private SleepStatisticsResponse createEmptyStatisticsResponse(DateRange dateRange) {
        Map<Feeling, Integer> emptyFeelingCounts = new EnumMap<>(Feeling.class);
        for (Feeling feeling : Feeling.values()) {
            emptyFeelingCounts.put(feeling, 0);
        }

        SleepStatisticsResponse response = new SleepStatisticsResponse();
        response.setDateRange(dateRange);
        response.setAverageTotalTimeInBedMinutes(0.0);
        response.setAverageBedTime(null);
        response.setAverageWakeTime(null);
        response.setFeelingCounts(emptyFeelingCounts);
        return response;
    }

    /**
     * Calculates the average sleep duration in minutes from a list of sleep logs.
     *
     * @param sleepLogs the list of sleep logs
     * @return the average sleep duration in minutes
     */
    private double calculateAverageSleepDuration(List<SleepLog> sleepLogs) {
        return sleepLogs.stream()
                .mapToInt(SleepLog::getTotalTimeInBedMinutes)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates average bed and wake times from a list of sleep logs.
     *
     * @param sleepLogs the list of sleep logs
     * @return an AverageSleepTimes object containing average bed time and wake time
     */
    private AverageSleepTimes calculateAverageBedAndWakeTimes(List<SleepLog> sleepLogs) {
        ZoneId zoneId = ZoneId.systemDefault();

        // Convert Instant to LocalTime for bed times
        List<LocalTime> bedTimes = sleepLogs.stream()
                .map(log -> LocalTime.from(log.getBedTime().atZone(zoneId)))
                .toList();

        // Convert Instant to LocalTime for wake times
        List<LocalTime> wakeTimes = sleepLogs.stream()
                .map(log -> LocalTime.from(log.getWakeTime().atZone(zoneId)))
                .toList();

        // Calculate average times
        LocalTime averageBedTime = calculateAverageTime(bedTimes);
        LocalTime averageWakeTime = calculateAverageTime(wakeTimes);

        return new AverageSleepTimes(averageBedTime, averageWakeTime);
    }

    /**
     * Counts the occurrences of each feeling from a list of sleep logs.
     *
     * @param sleepLogs the list of sleep logs
     * @return a map of feelings and their counts
     */
    private Map<Feeling, Integer> countFeelings(List<SleepLog> sleepLogs) {
        // Count each feeling
        Map<Feeling, Integer> feelingCounts = sleepLogs.stream()
                .collect(Collectors.groupingBy(
                        SleepLog::getFeeling,
                        Collectors.summingInt(log -> 1)
                ));

        // Ensure all feelings are represented in the map
        for (Feeling feeling : Feeling.values()) {
            feelingCounts.putIfAbsent(feeling, 0);
        }

        return feelingCounts;
    }

    /**
     * Builds a sleep statistics response from the calculated statistics.
     *
     * @param dateRange the date range for statistics
     * @param averageTotalTimeInBedMinutes the average sleep duration in minutes
     * @param averageBedTime the average bed time
     * @param averageWakeTime the average wake time
     * @param feelingCounts the map of feelings and their counts
     * @return the sleep statistics response
     */
    private SleepStatisticsResponse buildStatisticsResponse(
            DateRange dateRange,
            double averageTotalTimeInBedMinutes,
            LocalTime averageBedTime,
            LocalTime averageWakeTime,
            Map<Feeling, Integer> feelingCounts) {

        SleepStatisticsResponse response = new SleepStatisticsResponse();
        response.setDateRange(dateRange);
        response.setAverageTotalTimeInBedMinutes(averageTotalTimeInBedMinutes);
        response.setAverageBedTime(averageBedTime);
        response.setAverageWakeTime(averageWakeTime);
        response.setFeelingCounts(feelingCounts);
        return response;
    }

    /**
     * Calculates the average time from a list of LocalTime objects.
     *
     * @param times the list of LocalTime objects
     * @return the average time
     */
    private LocalTime calculateAverageTime(List<LocalTime> times) {
        if (times.isEmpty()) {
            return null;
        }

        // Convert times to seconds since midnight
        long totalSeconds = times.stream()
                .mapToLong(LocalTime::toSecondOfDay)
                .sum();

        // Calculate average seconds
        long averageSeconds = totalSeconds / times.size();

        // Convert back to LocalTime
        return LocalTime.ofSecondOfDay(averageSeconds);
    }
}
