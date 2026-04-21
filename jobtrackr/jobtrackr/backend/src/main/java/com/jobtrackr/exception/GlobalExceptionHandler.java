package com.jobtrackr.exception;

import com.jobtrackr.dto.JobApplicationDTOs.ErrorResponse;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler – Centralised error handling for the REST API.
 *
 * OOP principle: Single Responsibility — one place handles all exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateApplicationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(error(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                (a, b) -> a
            ));
        ErrorResponse body = error(400, "Validation Failed", "Request validation failed");
        body.setFieldErrors(fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
            .body(error(500, "Internal Server Error", ex.getMessage()));
    }

    private ErrorResponse error(int status, String error, String message) {
        return ErrorResponse.builder()
            .status(status).error(error).message(message)
            .timestamp(LocalDateTime.now()).build();
    }
}
