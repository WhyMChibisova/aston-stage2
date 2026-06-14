package ru.aston.hometask.notificationservice.dto;

public record UserEventDto(
        String email,
        EventType eventType
) {
    public enum EventType {
        CREATED,
        DELETED
    }
}