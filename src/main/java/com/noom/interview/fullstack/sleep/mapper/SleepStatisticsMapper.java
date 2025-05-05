package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.domain.entity.*;
import org.mapstruct.*;

import java.util.*;

/**
 * MapStruct mapper for converting sleep logs to statistics response.
 */
@Mapper(componentModel = "spring")
public interface SleepStatisticsMapper {

    /**
     * Creates a SleepStatisticsResponse from a list of SleepLog entities, a date range,
     * average times, and feeling frequencies.
     * 
     * @param sleepLogs the list of SleepLog entities
     * @param dateRange the date range for the statistics
     * @param averageBedTime the average bedtime as a string in "HH:mm:ss" format
     * @param averageWakeTime the average wake time as a string in "HH:mm:ss" format
     * @param feelingFrequencies the frequencies of each feeling
     * @return the SleepStatisticsResponse DTO
     */
    @Mapping(target = "averageTotalTimeInBedMinutes",
            expression = "java(calculateAverageTotalTimeInBedMinutes(sleepLogs))")
    @Mapping(target = "feelingCounts", source = "feelingFrequencies")
    SleepStatisticsResponse toResponse(List<SleepLog> sleepLogs, 
                                      DateRange dateRange,
                                      String averageBedTime,
                                      String averageWakeTime,
                                      Map<Feeling, Integer> feelingFrequencies);

    /**
     * Calculates the average total time in bed in minutes from a list of SleepLog entities.
     * 
     * @param sleepLogs the list of SleepLog entities
     * @return the average total time in bed in minutes as a Double
     */
    default Double calculateAverageTotalTimeInBedMinutes(List<SleepLog> sleepLogs) {
        if (sleepLogs == null || sleepLogs.isEmpty()) {
            return 0.0;
        }

        return sleepLogs.stream()
                .mapToInt(SleepLog::getTotalTimeInBedMinutes)
                .average()
                .orElse(0.0);
    }
}
