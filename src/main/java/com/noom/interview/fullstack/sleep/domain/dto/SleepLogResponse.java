package com.noom.interview.fullstack.sleep.domain.dto;

import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import lombok.*;

import java.time.*;

/**
 * DTO for returning sleep log data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepLogResponse {
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;
    private Integer totalTimeInBedMinutes;
    private Feeling feeling;
}