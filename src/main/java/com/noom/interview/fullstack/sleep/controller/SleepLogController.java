package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.domain.dto.*;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing sleep logs.
 */
@RestController
@RequestMapping("/api/sleep-logs")
@RequiredArgsConstructor
@Tag(name = "Sleep Logs", description = "API for managing sleep logs")
public class SleepLogController {

    private final SleepLogService sleepLogService;

    /**
     * Creates a new sleep log.
     *
     * @param request the sleep log request
     * @param userId the ID of the user (from header)
     * @return the created sleep log response with status 201 (Created)
     */
    @PostMapping
    @Operation(summary = "Create a new sleep log", description = "Creates a new sleep log for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sleep log created",
                    content = @Content(schema = @Schema(implementation = SleepLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Sleep log already exists for the given user and date")
    })
    public ResponseEntity<SleepLogResponse> createSleepLog(
            @Valid @RequestBody SleepLogRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        request.setUserId(userId);

        SleepLogResponse response = sleepLogService.createSleepLog(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}