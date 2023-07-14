package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;
    private Long userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        userDto = UserDto.builder().build();
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoke_thenStatusOK() {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(userClient).getUsers(1, 1);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenNotValidParamFrom_thenStatusBadRequest() {
        mockMvc.perform(get("/users")
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUsers(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenNotValidParamSize0_thenStatusBadRequest() {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUsers(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenNotValidParamSizeNegative_thenStatusBadRequest() {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUsers(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllUsers_withoutParams_thenStatusOk() {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getUsers(defaultFrom, defaultSize);
    }


    @SneakyThrows
    @Test
    void getUser_whenInvoke_thenStatusOk() {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getUser_whenUserNotFound_thenStatusNotFound() {
        when(userClient.getUser(any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userClient).getUser(userId);
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenStatus2xx() {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).create(userDto);
    }

    @SneakyThrows
    @Test
    void create_whenNotValidUserName_thenStatusBadRequest() {
        userDto = UserDto.builder()
                .email("user@mail.ru").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @SneakyThrows
    @Test
    void create_whenNotValidUserEmail_thenStatusBadRequest() {
        userDto = UserDto.builder()
                .name("User")
                .email(" ").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @SneakyThrows
    @Test
    void create_whenEmptyBody_thenStatusBadRequest() {
        mockMvc.perform(post("/users")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenStatus2xx() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void update_whenInvoke_thenStatus2xx() {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).update(userId, userDto);
    }

    @SneakyThrows
    @Test
    void update_whenUserNotFound_thenStatusNotFound() {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();
        when(userClient.update(userId, userDto))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userClient).update(userId, userDto);
    }

    @SneakyThrows
    @Test
    void update_whenEmptyBody_thenStatusBadRequest() {
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(userId, userDto);
    }
}