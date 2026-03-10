package org.example.hateoas;

import org.example.controller.UserController;
import org.example.dto.UserDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(UserDto user) {

        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }
}
