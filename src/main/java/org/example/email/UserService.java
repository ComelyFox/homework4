package org.example.email;

import org.example.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto request);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto request);

    boolean deleteUser(Long id);
}
