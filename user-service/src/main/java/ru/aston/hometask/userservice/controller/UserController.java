package ru.aston.hometask.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.hometask.userservice.dto.ExceptionResponse;
import ru.aston.hometask.userservice.dto.UserRequest;
import ru.aston.hometask.userservice.dto.UserResponse;
import ru.aston.hometask.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "User management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/users")
public class UserController {
    public static final String OK_CODE = "200";
    public static final String CREATED_CODE = "201";
    public static final String NO_CONTENT_CODE = "204";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String NOT_FOUND_CODE = "404";
    public static final String USER_CREATED_MSG = "User created";
    public static final String INVALID_USER_DATA_MSG = "Invalid user data";
    public static final String USER_FOUND_MSG = "User found";
    public static final String USER_NOT_FOUND_MSG = "User not found";
    public static final String USER_UPDATED_MSG = "User updated";
    public static final String USER_DELETED_MSG = "User deleted";

    @Autowired
    private UserService userService;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = CREATED_CODE, description = USER_CREATED_MSG,
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = BAD_REQUEST_CODE, description = INVALID_USER_DATA_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(
            responseCode = OK_CODE, description = USER_FOUND_MSG,
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = OK_CODE, description = USER_FOUND_MSG,
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = NOT_FOUND_CODE, description = USER_NOT_FOUND_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = OK_CODE, description = USER_UPDATED_MSG,
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = BAD_REQUEST_CODE, description = INVALID_USER_DATA_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(
                    responseCode = NOT_FOUND_CODE, description = USER_NOT_FOUND_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = USER_DELETED_MSG),
            @ApiResponse(
                    responseCode = NOT_FOUND_CODE, description = USER_NOT_FOUND_MSG,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}