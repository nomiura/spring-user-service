package ru.test.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.test.dto.UserRequestDto;
import ru.test.dto.UserResponseDto;
import ru.test.exception.InvalidUserDataException;
import ru.test.exception.UserNotFoundException;
import ru.test.model.User;
import ru.test.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void createUserShouldThrowIfEmailExists() {
        UserRequestDto request =
                new UserRequestDto("Zheka", "zheka@mail.ru", 10);

        when(repository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(User.builder().build()));

        assertThrows(InvalidUserDataException.class,
                () -> service.createUser(request));

        verify(repository, never()).save(any());
    }

    @Test
    void createUserShouldReturnResponseDto() {
        UserRequestDto request =
                new UserRequestDto("Mikhail", "tarkov@oneshoot.com", 29);

        when(repository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserResponseDto result = service.createUser(request);

        assertEquals("Mikhail", result.getName());
        assertEquals("tarkov@oneshoot.com", result.getEmail());
        assertEquals(29, result.getAge());

        verify(repository).save(any(User.class));
    }

    @Test
    void getAllUsersShouldReturnListOfResponseDto() {
        User user =
                User.builder().id(1L)
                        .name("Natalia")
                        .email("genshin@mail.com")
                        .age(30)
                        .build();

        when(repository.findAll()).thenReturn(List.of(user));

        List<UserResponseDto> result = service.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Natalia", result.get(0).getName());
        assertEquals("genshin@mail.com", result.get(0).getEmail());
        assertEquals(30, result.get(0).getAge());

        verify(repository).findAll();
    }

    @Test
    void getUserByIdShouldReturnDto() {
        User user =
                User.builder().id(1L)
                        .name("Ivan")
                        .email("ivan@mail.com")
                        .age(25)
                        .build();

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = service.getUserById(1L);

        assertEquals("Ivan", result.getName());
        assertEquals("ivan@mail.com", result.getEmail());
        assertEquals(25, result.getAge());
    }

    @Test
    void getUserByIdShouldThrowIfNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getUserById(1L));
    }

    @Test
    void updateUserShouldUpdateFields() {
        Long id = 1L;

        User existing =
                User.builder().id(id)
                        .name("Old")
                        .email("old@mail.com")
                        .age(20)
                        .build();

        UserRequestDto request =
                new UserRequestDto("New", "new@mail.com", 30);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.findByEmail("new@mail.com"))
                .thenReturn(Optional.empty());
        when(repository.save(existing)).thenReturn(existing);

        UserResponseDto result = service.updateUser(id, request);

        assertEquals("New", existing.getName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals(30, existing.getAge());

        assertEquals("New", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals(30, result.getAge());
    }

    @Test
    void updateUserShouldThrowIfEmailExists() {
        Long id = 1L;

        User existing =
                User.builder().id(id)
                        .name("Old")
                        .email("old@mail.com")
                        .age(20)
                        .build();

        User anotherUser =
                User.builder().id(2L)
                        .email("new@mail.com")
                        .build();

        UserRequestDto request =
                new UserRequestDto("New", "new@mail.com", 30);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.findByEmail("new@mail.com"))
                .thenReturn(Optional.of(anotherUser));

        assertThrows(InvalidUserDataException.class,
                () -> service.updateUser(id, request));
    }

    @Test
    void deleteUserShouldDeleteIfExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteUserShouldThrowIfNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.deleteUser(1L));
    }
}