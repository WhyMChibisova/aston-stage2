package ru.aston.hometask.userservice.dto;

public record UserEventDto(
        String email,
        EventType eventType
) {
    public enum EventType {
        CREATED,
        DELETED
    }
}