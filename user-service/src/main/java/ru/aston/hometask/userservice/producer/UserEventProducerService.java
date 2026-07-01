package ru.aston.hometask.userservice.producer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.aston.hometask.userservice.dto.UserEventDto;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class UserEventProducerService {
    private static final String KAFKA_SEND_MSG_ERROR = "Failed to send Kafka event %s for %s: %s";
    private static final String KAFKA_SEND_MSG_SUCCESS = "Sent Kafka event %s for %s to partition %s";
    private static final String KAFKA_FALLBACK_MSG = "Circuit breaker %s open";
    private static final String KAFKA_CB_NAME = "kafkaProducerCb";

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @Value("${app.kafka.topic.user-events}")
    private String topic;

    public CompletableFuture<SendResult<String, UserEventDto>> sendUserCreated(String email) {
        return send(email, UserEventDto.EventType.CREATED);
    }

    public CompletableFuture<SendResult<String, UserEventDto>> sendUserDeleted(String email) {
        return send(email, UserEventDto.EventType.DELETED);
    }

    @CircuitBreaker(name = KAFKA_CB_NAME, fallbackMethod = "sendKafkaFallback")
    public CompletableFuture<SendResult<String, UserEventDto>> send(String email, UserEventDto.EventType eventType) {
        UserEventDto userEventDto = new UserEventDto(email, eventType);
        CompletableFuture<SendResult<String, UserEventDto>> future = kafkaTemplate.send(topic, email, userEventDto)
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
        return future;
    }

    public CompletableFuture<SendResult<String, UserEventDto>> sendKafkaFallback(
            String email, UserEventDto.EventType eventType, Throwable t) {
        log.warn(String.format(KAFKA_FALLBACK_MSG, KAFKA_CB_NAME));
        return CompletableFuture.failedFuture(t);
    }
}