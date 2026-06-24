package ru.aston.hometask.userservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    public static final String BAD_REQUEST_CODE = "400";
    public static final String NOT_FOUND_CODE = "404";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String INVALID_DATA_MSG = "Invalid data";
    public static final String NOT_FOUND_MSG = "Not found";
    public static final String VALIDATION_EXCEPTION_MSG = "Validation exceptions @Valid";
    public static final String UNEXPECTED_SERVER_ERROR_MSG = "An unexpected server error occurred";

    @ApiResponse(
            responseCode = BAD_REQUEST_CODE, description = INVALID_DATA_MSG,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ExceptionResponse handleBadRequest(BadRequestException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ApiResponse(
            responseCode = NOT_FOUND_CODE, description = NOT_FOUND_MSG,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionResponse handleNotFound(ResourceNotFoundException e) {
        return new ExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

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