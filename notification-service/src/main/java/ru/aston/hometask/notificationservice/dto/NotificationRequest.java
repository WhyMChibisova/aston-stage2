package ru.aston.hometask.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Notification request")
public record NotificationRequest(
        @Schema(description = "Notification email", example = "chibisova3586@gmail.com")
        @NotBlank(message = "Email can't be blank")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "Event type: CREATED/DELETED", example = "CREATED")
        @NotNull(message = "Event type can't be null")
        UserEventDto.EventType eventType
) {}