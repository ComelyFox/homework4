package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.dto.UserDto;
import org.example.hateoas.UserModelAssembler;
import org.example.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API для работы с пользователями")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserModelAssembler assembler;

    public UserController(UserService userService, UserModelAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @Operation(summary = "Получить список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {

        log.info("API: запрос списка всех пользователей");

        List<EntityModel<UserDto>> users = userService.getAllUsers()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDto>> collection =
                CollectionModel.of(users);

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable Long id) {

        log.info("API: запрос пользователя по id={}", id);

        UserDto user = userService.getUserById(id);

        if (user == null) {
            log.warn("API: пользователь не найден id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(assembler.toModel(user));
    }

    @Operation(summary = "Создать нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных пользователя")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserDto request) {

        log.info("API: создание пользователя name={}, email={}", request.name(), request.email());

        UserDto created = userService.createUser(request);

        URI location = URI.create("/api/users/" + created.id());

        return ResponseEntity
                .created(location)
                .body(assembler.toModel(created));
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto request) {

        log.info("API: обновление пользователя id={}", id);

        UserDto updated = userService.updateUser(id, request);

        if (updated == null) {
            log.warn("API: пользователь для обновления не найден id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        log.info("API: удаление пользователя id={}", id);

        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            log.warn("API: пользователь для удаления не найден id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}

