package org.example.dto;

import java.time.LocalDateTime;

public record UserDto(Long id, String name, Integer age, String email, LocalDateTime createdAt) {
}
