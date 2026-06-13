package ru.aston.hometask.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDto(
        @NotBlank(message = "Name can't be blank")
        @Size(max = 100, message = "Name can't contain more than 100 characters")
        String name,

        @NotBlank(message = "Email can't be blank")
        @Email
        @Size(max = 100, message = "Email can't contain more than 100 characters")
        String email,

        @NotNull
        @Positive(message = "Age must be positive")
        @Max(value = 150, message = "Age can't be more than 150")
        Integer age
) {}