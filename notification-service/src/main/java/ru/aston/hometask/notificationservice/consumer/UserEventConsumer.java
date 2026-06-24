package ru.aston.hometask.notificationservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.hometask.notificationservice.dto.UserEventDto;
import ru.aston.hometask.notificationservice.service.NotificationService;

@Slf4j
@Component
public class UserEventConsumer {
    private static final String KAFKA_RECEIVED_MSG = "Received Kafka event $s for %s";
    private static final String NOTIFICATION_SEND_SUCCESS = "Notification sent to %s for event %s";
    private static final String NOTIFICATION_SEND_ERROR = "Failed to send notification event %s for %s: %s";

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(
            topics = "${app.kafka.topic.user-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserEventDto dto) {
        log.info(String.format(KAFKA_RECEIVED_MSG, dto.eventType(), dto.email()));
        try {
            notificationService.sendNotification(dto.email(), dto.eventType());
            log.info(String.format(NOTIFICATION_SEND_SUCCESS, dto.email(), dto.eventType()));
        } catch (Exception e) {
            log.error(String.format(NOTIFICATION_SEND_ERROR, dto.eventType(), dto.email(), e.getMessage()));
        }
    }
}