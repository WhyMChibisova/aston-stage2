package ru.aston.hometask.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "User response")
public record UserResponse(
        @Schema(description = "User id", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID id,

        @Schema(description = "User name", example = "Chibisova Masha")
        String name,

        @Schema(description = "User email", example = "chibisova3586@gmail.com")
        String email,

        @Schema(description = "User age", example = "23")
        Integer age
) {}