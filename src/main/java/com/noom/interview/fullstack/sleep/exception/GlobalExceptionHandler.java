package com.noom.interview.fullstack.sleep.exception;


import com.noom.interview.fullstack.sleep.domain.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application. Catches exceptions and converts them
 * into standardized ErrorResponse objects.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions (e.g., from @Valid annotations).
     * Returns HTTP 400 Bad Request.
     */
    @ApiResponse(responseCode = "400", description = "Invalid request data provided",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException (often used for bad parameters).
     * Returns HTTP 400 Bad Request.
     */
    @ApiResponse(responseCode = "400", description = "Invalid argument provided",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MissingRequestHeaderException.
     * Returns HTTP 400 Bad Request.
     */
    @ApiResponse(responseCode = "400", description = "Required request header is missing",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
            MissingRequestHeaderException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalStateException (often indicates an invalid state for the requested operation).
     * Returns HTTP 409 Conflict.
     */
    @ApiResponse(responseCode = "409", description = "Operation cannot be performed in the current state",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles DataIntegrityViolationException (e.g., unique constraint violations).
     * Returns HTTP 409 Conflict.
     */
    @ApiResponse(responseCode = "409", description = "Data integrity constraint violation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "Data integrity violation occurred. Please check your input.";
        LOGGER.warn("Data integrity violation: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                message,
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles ResponseStatusException, using the status and reason from the exception.
     * Useful when exceptions are thrown directly with HTTP status (e.g., via Optional.orElseThrow).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getReason(),
                request.getRequestURI());

        if (status.is5xxServerError()) {
            LOGGER.error("ResponseStatusException occurred: Status={}, Reason='{}'", status, ex.getReason(), ex);
        } else if (status.is4xxClientError()) {
            LOGGER.warn("ResponseStatusException occurred: Status={}, Reason='{}'", status, ex.getReason());
        } else {
            LOGGER.info("ResponseStatusException occurred: Status={}, Reason='{}'", status, ex.getReason());
        }


        return new ResponseEntity<>(errorResponse, status); // Use status from exception
    }
    /**
     * Handles all other uncaught exceptions.
     * Returns HTTP 500 Internal Server Error.
     */
    @ApiResponse(responseCode = "500", description = "An unexpected internal server error occurred",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        LOGGER.error("An unexpected error occurred processing request {}", request.getRequestURI(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected internal error occurred. Please try again later.",
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}