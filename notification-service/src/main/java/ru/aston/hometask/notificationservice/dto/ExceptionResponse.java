package ru.aston.hometask.notificationservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ExceptionResponse(
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {}