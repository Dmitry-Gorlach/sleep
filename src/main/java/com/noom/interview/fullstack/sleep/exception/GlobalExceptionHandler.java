package com.noom.interview.fullstack.sleep.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @return the response entity with status 400 and error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Bad Request");
        errorResponse.put(MESSAGE_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalStateException and returns a 409 Conflict response.
     *
     * @param ex the exception
     * @return the response entity with status 409 and error message
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Conflict");
        errorResponse.put(MESSAGE_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles DataIntegrityViolationException and returns a 409 Conflict response.
     *
     * @param ex the exception
     * @return the response entity with status 409 and error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>>
    handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Conflict");
        errorResponse.put(MESSAGE_KEY, "Data integrity violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles MissingRequestHeaderException and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @return the response entity with status 400 and error message
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Bad Request");
        errorResponse.put(MESSAGE_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles ResponseStatusException and returns a response with the status code from the exception.
     *
     * @param ex the exception
     * @return the response entity with the status code from the exception and error message
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, ex.getStatusCode().toString());
        errorResponse.put(MESSAGE_KEY, ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * Handles all other exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the exception
     * @return the response entity with status 500 and error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_KEY, "Internal Server Error");
        errorResponse.put(MESSAGE_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
