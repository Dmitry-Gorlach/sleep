package com.noom.interview.fullstack.sleep.domain.dto;

import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import lombok.*;

import java.util.Map;

/**
 * DTO for returning sleep statistics data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepStatisticsResponse {
    private DateRange dateRange;
    private Integer averageTotalTimeInBedMinutes;
    private String averageBedTime;   // "HH:mm:ss"
    private String averageWakeTime;  // "HH:mm:ss"
    private Map<Feeling, Integer> feelingFrequencies;
}