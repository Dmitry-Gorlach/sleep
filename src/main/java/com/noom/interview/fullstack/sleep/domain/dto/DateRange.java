package com.noom.interview.fullstack.sleep.domain.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * Represents a date range with start and end dates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
    private LocalDate startDate;
    private LocalDate endDate;
}