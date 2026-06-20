package ru.aston.hometask.notificationservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.hometask.notificationservice.dto.NotificationRequest;
import ru.aston.hometask.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void send(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request.email(), request.eventType());
    }
}