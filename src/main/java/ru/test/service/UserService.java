package ru.test.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.test.dto.UserRequestDto;
import ru.test.dto.UserResponseDto;
import ru.test.exception.InvalidUserDataException;
import ru.test.exception.UserNotFoundException;
import ru.test.kafka.UserEventProducer;
import ru.test.model.User;
import ru.test.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserEventProducer userEventProducer;

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        repository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new InvalidUserDataException("Email уже существует");
        });
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
        User saved = repository.save(user);
       // log.info("Пользователь создан: {}", saved.getId());
        userEventProducer.sendUserCreated(saved.getEmail());
        return toDto(saved);
    }

    @Transactional()
    public UserResponseDto getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return toDto(user);
    }

    @Transactional()
    public List<UserResponseDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        repository.findByEmail(dto.getEmail())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new InvalidUserDataException("Email уже существует");
                });

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());

        User updated = repository.save(user);
        log.info("Пользователь обновлён: {}", updated.getId());
        return toDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow();
        userEventProducer.sendUserDeleted(user.getEmail());
        if (!repository.existsById(id)) throw new UserNotFoundException("Пользователь не найден");
        repository.deleteById(id);
        //log.info("Пользователь удалён: {}", id);
    }

    private UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }
}
