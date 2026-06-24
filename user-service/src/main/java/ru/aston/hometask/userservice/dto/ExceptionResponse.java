package ru.aston.hometask.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Exception response")
public record ExceptionResponse(
        @Schema(description = "Exception message", example = "User not found: f47ac10b-58cc-4372-a567-0e02b2c3d479")
        String message,

        @Schema(description = "Exception occurrence dateTime", example = "2026-06-01T12:00:00")
        LocalDateTime timestamp,

        @Schema(description = "Validation exceptions by fields",
                example = "{\\\"email\\\": \\\"Email can't be blank\\\", \\\"age\\\": \\\"Age must be positive\\\"}",
                nullable = true)
        Map<String, String> errors
) {}