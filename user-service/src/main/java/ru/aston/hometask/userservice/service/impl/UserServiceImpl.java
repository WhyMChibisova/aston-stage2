package ru.aston.hometask.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.hometask.userservice.dao.UserRepository;
import ru.aston.hometask.userservice.dto.UserDto;
import ru.aston.hometask.userservice.exception.BadRequestException;
import ru.aston.hometask.userservice.exception.ResourceNotFoundException;
import ru.aston.hometask.userservice.model.User;
import ru.aston.hometask.userservice.producer.UserEventProducerService;
import ru.aston.hometask.userservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.aston.hometask.userservice.mapper.UserMapper.toListUserDto;
import static ru.aston.hometask.userservice.mapper.UserMapper.toUserDto;
import static ru.aston.hometask.userservice.mapper.UserMapper.toUserEntity;

@Service
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MSG = "User not found: %s";
    private static final String EMAIL_DUPLICATE_MSG = "User email already exists: %s";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventProducerService userEventProducer;

    @Override
    public UserDto create(UserDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException(String.format(EMAIL_DUPLICATE_MSG, dto.email()));
        }
        User savedUser = userRepository.save(toUserEntity(dto));
        userEventProducer.sendUserCreated(savedUser.getEmail());
        return toUserDto(savedUser);
    }

    @Override
    public List<UserDto> getAll() {
        return toListUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        return toUserDto(user);
    }

    @Override
    public UserDto update(UUID id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        Optional<User> userByEmail = userRepository.findByEmail(dto.email());

        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(user.getId())) {
            throw new BadRequestException(String.format(EMAIL_DUPLICATE_MSG, dto.email()));
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        return toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        userRepository.deleteById(id);
        userEventProducer.sendUserDeleted(user.getEmail());
    }
}