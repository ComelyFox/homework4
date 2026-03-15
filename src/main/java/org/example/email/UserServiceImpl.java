package org.example.email;

import org.example.entity.Users;
import org.example.dto.UserDto;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Сервис: создание пользователя name={}, email={}, age={}",
                userDto.name(), userDto.email(), userDto.age());

        if (userRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        Users user = new Users(userDto.name(), userDto.email(), userDto.age());
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        Optional<Users> userOpt = userRepository.findById(id);
        log.info("Сервис: поиск пользователя по id={}", id);
        if (userOpt.isEmpty()) {
            log.info("Сервис: пользователь не найден id={}", id);
            return null;
        }

        return toDto(userOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Сервис: получение всех пользователей");
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Сервис: обновление пользователя id={} (name={}, email={}, age={})",
                id, userDto.name(), userDto.email(), userDto.age());

        Optional<Users> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) {
            log.info("Сервис: пользователь для обновления не найден id={}", id);
            return null;
        }

        Users user = existingOpt.get();

        if (userDto.name() != null) {
            user.setName(userDto.name());
        }
        if (userDto.email() != null) {
            user.setEmail(userDto.email());
        }
        if (userDto.age() != null) {
            user.setAge(userDto.age());
        }

        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        log.info("Сервис: удаление пользователя id={}", id);

        if (!userRepository.existsById(id)) {
            log.info("Сервис: пользователь для удаления не найден id={}", id);
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    private UserDto toDto(Users user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
