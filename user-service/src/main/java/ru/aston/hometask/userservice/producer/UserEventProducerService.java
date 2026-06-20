package ru.aston.hometask.userservice.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aston.hometask.userservice.dto.UserEventDto;

@Slf4j
@Component
public class UserEventProducerService {
    private static final String KAFKA_SEND_MSG_ERROR = "Failed to send Kafka event %s for %s: %s";
    private static final String KAFKA_SEND_MSG_SUCCESS = "Sent Kafka event %s for %s to partition %s";

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @Value("${app.kafka.topic.user-events}")
    private String topic;

    public void sendUserCreated(String email) {
        send(email, UserEventDto.EventType.CREATED);
    }

    public void sendUserDeleted(String email) {
        send(email, UserEventDto.EventType.DELETED);
    }

    private void send(String email, UserEventDto.EventType eventType) {
        UserEventDto userEventDto = new UserEventDto(email, eventType);
        kafkaTemplate.send(topic, email, userEventDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(String.format(KAFKA_SEND_MSG_ERROR,
                                eventType, email, ex.getMessage())
                        );
                    } else {
                        log.info(String.format(KAFKA_SEND_MSG_SUCCESS,
                                eventType, email, result.getRecordMetadata().partition())
                        );
                    }
                });
    }
}