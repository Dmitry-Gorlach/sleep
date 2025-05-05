package com.noom.interview.fullstack.sleep.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a date range with from and to dates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Date range for statistics calculation")
public class DateRange {
    @Schema(description = "Start date of the range (inclusive)")
    private LocalDate from;

    @Schema(description = "End date of the range (inclusive)")
    private LocalDate to;
}
