package ru.aston.hometask.notificationservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.hometask.notificationservice.dto.UserEventDto;
import ru.aston.hometask.notificationservice.service.NotificationService;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final String SUBJECT_CREATED = "Welcome!";
    private static final String SUBJECT_DELETED = "Account deleted";
    private static final String TEXT_CREATED = "Hello! Your account on the website has been successfully created";
    private static final String TEXT_DELETED = "Hello! Your account has been deleted";
    private static final String NOTIFICATION_SEND_SUCCESS = "Notification sent to %s for event %s";
    private static final String NOTIFICATION_SEND_ERROR = "Unknown event type: %s";

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendNotification(String email, UserEventDto.EventType eventType) {
        String subject;
        String text;

        switch (eventType) {
            case CREATED -> {
                subject = SUBJECT_CREATED;
                text = TEXT_CREATED;
            }
            case DELETED -> {
                subject = SUBJECT_DELETED;
                text = TEXT_DELETED;
            }
            default -> throw new IllegalArgumentException(String.format(NOTIFICATION_SEND_ERROR, eventType));
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        log.info(String.format(NOTIFICATION_SEND_SUCCESS, email, eventType));
    }
}