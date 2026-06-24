package ru.aston.hometask.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.aston.hometask.notificationservice.dto.UserEventDto;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = { "user-events-test" }
)
public class UserEventConsumerTest {
    private static final String SUBJECT_CREATED = "Welcome!";
    private static final String SUBJECT_DELETED = "Account deleted";
    private static final String TEXT_CREATED = "Hello! Your account on the website has been successfully created";
    private static final String TEXT_DELETED = "Hello! Your account has been deleted";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("test@localhost", "password"))
            .withPerMethodLifecycle(true);

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Value("${app.kafka.topic.user-events}")
    private String topic;

    private KafkaTemplate<String, UserEventDto> buildProducer() {
        Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafka);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    @Test
    void whenUserCreatedEvent_thenWelcomeEmailIsSent() throws Exception {
        UserEventDto event = new UserEventDto("created@example.com", UserEventDto.EventType.CREATED);

        buildProducer().send(topic, event).get();

        greenMail.waitForIncomingEmail(10_000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertEquals(1, messages.length);
        assertEquals("created@example.com", messages[0].getAllRecipients()[0].toString());
        assertEquals(SUBJECT_CREATED, messages[0].getSubject());
        assertEquals(TEXT_CREATED, messages[0].getContent().toString());
    }

    @Test
    void whenUserDeletedEventPublished_thenDeletionEmailIsSent() throws Exception {
        UserEventDto event = new UserEventDto("deleted@example.com", UserEventDto.EventType.DELETED);

        buildProducer().send(topic, event).get();

        greenMail.waitForIncomingEmail(10_000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertEquals(1, messages.length);
        assertEquals("deleted@example.com", messages[0].getAllRecipients()[0].toString());
        assertEquals(SUBJECT_DELETED, messages[0].getSubject());
        assertEquals(TEXT_DELETED, messages[0].getContent().toString());
    }
}