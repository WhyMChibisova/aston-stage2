package ru.aston.hometask.userservice.service;

import ru.aston.hometask.userservice.dto.UserRequest;
import ru.aston.hometask.userservice.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(UUID id);

    UserResponse update(UUID id, UserRequest request);

    void delete(UUID id);
}