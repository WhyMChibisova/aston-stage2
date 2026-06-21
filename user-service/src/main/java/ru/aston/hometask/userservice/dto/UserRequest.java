package ru.aston.hometask.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "User request")
public record UserRequest(
        @Schema(description = "User name", example = "Chibisova Masha")
        @NotBlank(message = "Name can't be blank")
        @Size(max = 100, message = "Name can't contain more than 100 characters")
        String name,

        @Schema(description = "User email", example = "chibisova3586@gmail.com")
        @NotBlank(message = "Email can't be blank")
        @Email
        @Size(max = 100, message = "Email can't contain more than 100 characters")
        String email,

        @Schema(description = "User age", example = "23")
        @NotNull
        @Positive(message = "Age must be positive")
        @Max(value = 150, message = "Age can't be more than 150")
        Integer age
) {}