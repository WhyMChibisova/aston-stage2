package ru.aston.hometask.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.hometask.notificationservice.dto.ExceptionResponse;
import ru.aston.hometask.notificationservice.dto.NotificationRequest;
import ru.aston.hometask.notificationservice.service.NotificationService;

@Tag(name = "Notifications", description = "Manually send email")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    public static final String NO_CONTENT_CODE = "204";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String SERVICE_UNAVAILABLE_CODE = "503";
    public static final String EMAIL_SENT_MSG = "Email sent";
    public static final String INVALID_DATA_MSG = "Invalid data";
    public static final String ERROR_SENDING_EMAIL_MSG = "Error sending email";
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Send email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = EMAIL_SENT_MSG),
            @ApiResponse(
                    responseCode = BAD_REQUEST_CODE, description = INVALID_DATA_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(
                    responseCode = SERVICE_UNAVAILABLE_CODE, description = ERROR_SENDING_EMAIL_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void send(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request.email(), request.eventType());
    }
}