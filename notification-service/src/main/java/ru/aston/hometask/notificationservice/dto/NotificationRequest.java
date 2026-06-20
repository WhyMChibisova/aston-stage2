package ru.aston.hometask.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Email must be valid")
    String email,

    @NotNull(message = "Event type can't be null")
    UserEventDto.EventType eventType
) {}