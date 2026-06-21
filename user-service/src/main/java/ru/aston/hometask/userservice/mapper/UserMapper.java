package ru.aston.hometask.userservice.mapper;

import ru.aston.hometask.userservice.dto.UserRequest;
import ru.aston.hometask.userservice.dto.UserResponse;
import ru.aston.hometask.userservice.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUserEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .age(request.age())
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }

    public static List<UserResponse> toListUserResponse(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}