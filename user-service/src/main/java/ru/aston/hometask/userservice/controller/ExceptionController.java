package ru.aston.hometask.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.aston.hometask.userservice.dto.ExceptionResponse;
import ru.aston.hometask.userservice.exception.BadRequestException;
import ru.aston.hometask.userservice.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ExceptionResponse handleBadRequest(BadRequestException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionResponse handleNotFound(ResourceNotFoundException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

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