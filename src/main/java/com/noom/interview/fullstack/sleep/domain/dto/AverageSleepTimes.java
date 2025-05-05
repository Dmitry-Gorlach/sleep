package com.noom.interview.fullstack.sleep.domain.dto;

import lombok.*;

import java.time.LocalTime;

/**
 * Represents average sleep times including bed time and wake time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageSleepTimes {
    /**
     * Average time when user goes to bed
     */
    private LocalTime bedTime;

    /**
     * Average time when user wakes up
     */
    private LocalTime wakeTime;
}