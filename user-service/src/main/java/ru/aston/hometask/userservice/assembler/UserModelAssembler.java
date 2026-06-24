package ru.aston.hometask.userservice.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.aston.hometask.userservice.controller.UserController;
import ru.aston.hometask.userservice.dto.UserResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getById(response.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all-users"),
                linkTo(methodOn(UserController.class).update(response.id(), null)).withRel("update"),
                linkTo(UserController.class).slash(response.id()).withRel("delete")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(Iterable<? extends UserResponse> entities) {
        CollectionModel<EntityModel<UserResponse>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(UserController.class).getAll()).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class).create(null)).withRel("create"));
        return collectionModel;
    }
}