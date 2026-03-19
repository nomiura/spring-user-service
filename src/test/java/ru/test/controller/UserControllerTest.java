package ru.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.test.dto.UserRequestDto;
import ru.test.dto.UserResponseDto;
import ru.test.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class UserControllerTest {

    private MockMvc mockMvc;
    private UserService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        service = Mockito.mock(UserService.class);
        UserController controller = new UserController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createUserShouldReturn201() throws Exception {
        UserRequestDto request = new UserRequestDto("Svetik", "steklosvetik@okr.ru", 40);
        UserResponseDto response = new UserResponseDto(1L, "Svetik", "steklosvetik@okr.ru", 40);

        when(service.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Svetik"))
                .andExpect(jsonPath("$.email").value("steklosvetik@okr.ru"))
                .andExpect(jsonPath("$.age").value(40));
    }

    @Test
    void getAllUsersShouldReturnList() throws Exception {
        List<UserResponseDto> users = List.of(
                new UserResponseDto(1L, "Natalia", "natalia@mail.com", 30),
                new UserResponseDto(2L, "Ivan", "ivan@mail.com", 25)
        );

        when(service.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Natalia"))
                .andExpect(jsonPath("$.content[0].email").value("natalia@mail.com"))
                .andExpect(jsonPath("$.content[0].age").value(30))
                .andExpect(jsonPath("$.content[1].name").value("Ivan"))
                .andExpect(jsonPath("$.content[1].email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.content[1].age").value(25));
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "Svetik", "steklosvetik@okr.ru", 40);

        when(service.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Svetik"))
                .andExpect(jsonPath("$.email").value("steklosvetik@okr.ru"))
                .andExpect(jsonPath("$.age").value(40));
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto("New", "new@mail.com", 30);
        UserResponseDto response = new UserResponseDto(1L, "New", "new@mail.com", 30);

        when(service.updateUser(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void deleteUserShouldReturn204() throws Exception {
        doNothing().when(service).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteUser(1L);
    }
}