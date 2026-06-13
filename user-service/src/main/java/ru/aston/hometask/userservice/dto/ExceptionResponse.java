package ru.aston.hometask.userservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ExceptionResponse(
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {}