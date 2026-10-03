package com.transithub.exception;

import com.transithub.dto.response.ApiErrorResponse;
import com.transithub.dto.response.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

/**
 * Catches exceptions from every controller and turns them into the same JSON error format.
 * Controllers and services just throw exceptions; they never build error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Our own exceptions: each one knows its HTTP status (404, 409, 400...). */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        return build(ex.getStatus(), ex.getMessage(), request, List.of());
    }

    /** @Valid failed on a request body: list every invalid field. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    /** The body is missing, is not valid JSON, or contains a value that does not fit (like an unknown enum). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "The request body is missing, is not valid JSON, or has an invalid value", request, List.of());
    }

    /** A URL parameter has the wrong type, for example ?status=FLYING. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Parameter '" + ex.getName() + "' has an invalid value", request, List.of());
    }

    /** A rule inside our entity classes was broken (for example origin equals destination). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                  HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
    }

    /** The database rejected the change (a safety net behind our own checks). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                HttpServletRequest request) {
        log.warn("Database constraint violation on {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "This change conflicts with existing data", request, List.of());
    }

    /**
     * Everything else. Spring's own web exceptions (wrong URL, wrong HTTP method, missing parameter,
     * unsupported media type...) already know their status, so we reuse it.
     * Anything unexpected becomes a 500 with a safe message; details only go to the log.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOthers(Exception ex, HttpServletRequest request) {
        if (ex instanceof ErrorResponse springError) {
            String detail = springError.getBody().getDetail();
            return build(springError.getStatusCode(),
                    detail != null ? detail : "The request could not be processed", request, List.of());
        }
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on the server", request, List.of());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatusCode status, String message,
                                                   HttpServletRequest request, List<FieldErrorDetail> errors) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(), message, Instant.now(), request.getRequestURI(), errors);
        return ResponseEntity.status(status).body(body);
    }
}
