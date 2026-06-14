package ru.aston.hometask.notificationservice.controller;

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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    private ExceptionResponse error(RuntimeException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }
}