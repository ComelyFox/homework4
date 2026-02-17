package org.example;

import org.example.controller.UserController;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserDto u1 = new UserDto(1L, "Alice",25,  "alice@example.com", LocalDateTime.now());
        UserDto u2 = new UserDto(2L, "Bob", 30, "bob@example.com", LocalDateTime.now());

        Mockito.when(userService.getAllUsers())
                .thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andExpect(jsonPath("$[1].email", is("bob@example.com")));
    }

    @Test
    void getUserById_whenFound_shouldReturnUser() throws Exception {
        UserDto dto = new UserDto(1L, "Alice", 25, "alice@example.com", LocalDateTime.now());

        Mockito.when(userService.getUserById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    void getUserById_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getUserById(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto created = new UserDto(1L, "Alice", 25, "alice@example.com", LocalDateTime.now());

        Mockito.when(userService.createUser(any(UserDto.class)))
                .thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    void updateUser_whenFound_shouldReturnUpdatedUser() throws Exception {
        UserDto updated = new UserDto(1L, "NewName", 26, "new@example.com", LocalDateTime.now());

        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NewName")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }

    @Test
    void updateUser_whenNotFound_shouldReturn404() throws Exception {
        UserDto request = new UserDto(255L, "NewName", 26, "new@example.com", LocalDateTime.now());

        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenFound_shouldReturn204() throws Exception {
        Mockito.when(userService.deleteUser(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(userService.deleteUser(1L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());
    }
}
