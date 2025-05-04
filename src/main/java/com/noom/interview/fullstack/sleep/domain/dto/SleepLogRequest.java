package com.noom.interview.fullstack.sleep.domain.dto;

import com.noom.interview.fullstack.sleep.domain.entity.Feeling;
import lombok.*;

import java.time.*;
import java.util.UUID;

/**
 * DTO for creating a new sleep log.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepLogRequest {
    private UUID userId;        // provided by client / header resolver
    private LocalDate sleepDate;
    private Instant bedTime;
    private Instant wakeTime;
    private Feeling feeling;
}