package ru.aston.hometask.userservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aston.hometask.userservice.dao.UserRepository;
import ru.aston.hometask.userservice.dto.UserRequest;
import ru.aston.hometask.userservice.dto.UserResponse;
import ru.aston.hometask.userservice.exception.BadRequestException;
import ru.aston.hometask.userservice.exception.ResourceNotFoundException;
import ru.aston.hometask.userservice.model.User;
import ru.aston.hometask.userservice.producer.UserEventProducerService;
import ru.aston.hometask.userservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.aston.hometask.userservice.mapper.UserMapper.toListUserResponse;
import static ru.aston.hometask.userservice.mapper.UserMapper.toUserEntity;
import static ru.aston.hometask.userservice.mapper.UserMapper.toUserResponse;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MSG = "User not found: %s";
    private static final String EMAIL_DUPLICATE_MSG = "User email already exists: %s";
    private static final String SEND_MSG_ERROR = "Kafka notification event was not delivered: %s";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventProducerService userEventProducer;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException(String.format(EMAIL_DUPLICATE_MSG, request.email()));
        }
        User savedUser = userRepository.save(toUserEntity(request));
        userEventProducer.sendUserCreated(savedUser.getEmail())
                .exceptionally(ex -> {
                    log.warn(String.format(SEND_MSG_ERROR, ex.getMessage()));
                    return null;
                });
        return toUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        return toListUserResponse(userRepository.findAll());
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        return toUserResponse(user);
    }

    @Override
    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        Optional<User> userByEmail = userRepository.findByEmail(request.email());

        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(user.getId())) {
            throw new BadRequestException(String.format(EMAIL_DUPLICATE_MSG, request.email()));
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
        return toUserResponse(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        userRepository.deleteById(id);
        userEventProducer.sendUserDeleted(user.getEmail())
                .exceptionally(ex -> {
                    log.warn(String.format(SEND_MSG_ERROR, ex.getMessage()));
                    return null;
                });
    }
}