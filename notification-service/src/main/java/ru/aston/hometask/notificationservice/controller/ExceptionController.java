package ru.aston.hometask.notificationservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.aston.hometask.notificationservice.dto.ExceptionResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    private static final String MAIL_ERROR = "Mail sending failed: %s";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String SERVICE_UNAVAILABLE_CODE = "503";
    public static final String VALIDATION_EXCEPTION_MSG = "Validation exceptions @Valid";
    public static final String ERROR_SENDING_EMAIL_MSG = "Error sending email";
    public static final String UNEXPECTED_SERVER_ERROR_MSG = "An unexpected server error occurred";

    @ApiResponse(
            responseCode = BAD_REQUEST_CODE, description = VALIDATION_EXCEPTION_MSG,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                errors
        );
    }

    @ApiResponse(
            responseCode = SERVICE_UNAVAILABLE_CODE, description = ERROR_SENDING_EMAIL_MSG,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(MailException.class)
    public ExceptionResponse handleMailException(MailException e) {
        log.error(String.format(MAIL_ERROR, e.getMessage()));
        return new ExceptionResponse(
                String.format(MAIL_ERROR, e.getMessage()),
                LocalDateTime.now(),
                null
        );
    }

    @ApiResponse(
            responseCode = INTERNAL_SERVER_ERROR_CODE, description = UNEXPECTED_SERVER_ERROR_MSG,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ExceptionResponse error(RuntimeException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }
}