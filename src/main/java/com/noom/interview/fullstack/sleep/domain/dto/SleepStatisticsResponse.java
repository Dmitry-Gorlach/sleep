package com.noom.interview.fullstack.sleep.domain.dto;

import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;
import java.util.Map;

/**
 * Response DTO for sleep statistics over a 30-day period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sleep statistics over a 30-day period")
public class SleepStatisticsResponse {

    @Schema(description = "Date range for which statistics were calculated")
    private DateRange dateRange;

    @Schema(description = "Average total time in bed in minutes")
    private Double averageTotalTimeInBedMinutes;

    @Schema(description = "Average bed time (local time)")
    private LocalTime averageBedTime;

    @Schema(description = "Average wake time (local time)")
    private LocalTime averageWakeTime;

    @Schema(description = "Count of each feeling (BAD, OK, GOOD)")
    private Map<Feeling, Integer> feelingCounts;
}
