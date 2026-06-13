package ru.aston.hometask.userservice.service;

import ru.aston.hometask.userservice.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(UserDto dto);
    List<UserDto> getAll();
    UserDto getById(UUID id);
    UserDto update(UUID id, UserDto dto);
    void delete(UUID id);
}