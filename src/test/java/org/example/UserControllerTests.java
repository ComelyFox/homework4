package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.UserController;
import org.example.dto.UserDto;
import org.example.hateoas.UserModelAssembler;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserModelAssembler.class) // Импортируем ассемблер, чтобы HATEOAS ссылки создавались корректно
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        UserDto user = new UserDto(1L, "Ivan", 25, "ivan@test.com", LocalDateTime.now());
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void createUser_ShouldReturn201() throws Exception {
        UserDto inputDto = new UserDto(null, "New User", 30, "new@test.com", null);
        UserDto savedDto = new UserDto(100L, "New User", 30, "new@test.com", LocalDateTime.now());

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/100"))
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void deleteUser_ShouldReturn404_WhenNotFound() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}