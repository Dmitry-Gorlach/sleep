package com.noom.interview.fullstack.sleep.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2023-10-27T10:15:30+01:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime timestamp;

    @Schema(description = "HTTP Status code", example = "400",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int status;

    @Schema(description = "General error category", example = "Bad Request",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String error;

    @Schema(description = "Specific error message detailing the issue", example = "Invalid input provided",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "The path where the error occurred", example = "/api/sleep-logs",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;
}
