package ru.aston.hometask.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.hometask.notificationservice.dto.NotificationRequest;
import ru.aston.hometask.notificationservice.dto.UserEventDto;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1)
public class NotificationServiceTest {
    private static final String END_POINT = "/api/notifications";
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
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void whenUserCreatedEvent_thenEmailWithWelcomeTextIsSent() throws Exception {
        NotificationRequest request = new NotificationRequest("created@example.com", UserEventDto.EventType.CREATED);
        mockMvc.perform(post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertEquals(1, messages.length);
        assertEquals("created@example.com", messages[0].getAllRecipients()[0].toString());
        assertEquals(SUBJECT_CREATED, messages[0].getSubject());
        assertEquals(TEXT_CREATED, messages[0].getContent().toString());
    }

    @Test
    void whenUserDeletedEvent_thenEmailWithDeletionTextIsSent() throws Exception {
        NotificationRequest request = new NotificationRequest("deleted@example.com", UserEventDto.EventType.DELETED);
        mockMvc.perform(post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertEquals(1, messages.length);
        assertEquals("deleted@example.com", messages[0].getAllRecipients()[0].toString());
        assertEquals(SUBJECT_DELETED, messages[0].getSubject());
        assertEquals(TEXT_DELETED, messages[0].getContent().toString());
    }

    @Test
    void whenEmailIsInvalid_thenBadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest("invalid.com", UserEventDto.EventType.CREATED);
        mockMvc.perform(post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEventTypeIsNull_thenBadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest("test@example.com", null);
        mockMvc.perform(post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}