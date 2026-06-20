package ru.aston.hometask.notificationservice.service;

import ru.aston.hometask.notificationservice.dto.UserEventDto;

public interface NotificationService {
    void sendNotification(String email, UserEventDto.EventType eventType);
}