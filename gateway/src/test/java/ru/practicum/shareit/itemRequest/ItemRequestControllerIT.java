package ru.practicum.shareit.itemRequest;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestClient itemRequestClient;
    private Long requestId;
    private Long userId;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        requestId = 0L;
        userId = 1L;
        itemRequestDto = ItemRequestDto.builder().build();
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenStatus2xx() {
        itemRequestDto.setDescription("Description");

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().is2xxSuccessful());

        verify(itemRequestClient).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void create_whenNotValidDescription_thenStatusBadRequest() {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void create_whenEmptyBody_thenStatusBadRequest() {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void create_whenEmptyUserId_thenStatusBadRequest() {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequestsByUser_whenInvoke_thenStatusOK() {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequestByUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequestsByUser_whenNotUserId_thenStatusBadRequest() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequestByUser(any());
    }

    @SneakyThrows
    @Test
    void getAllRequestsByUser_whenServerNotFoundUser_thenStatusNotFound() {
        when(itemRequestClient.getAllRequestByUser(any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getRequestById_whenInvoke_thenStatusOK() {
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(userId, requestId);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenNotUserId_thenStatusBadRequest() {
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(any(), any());
    }

    @SneakyThrows
    @Test
    void getRequestById_whenServerNotFoundResponseStatus_thenStatusNotFound() {
        when(itemRequestClient.getRequestById(any(), any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenInvoke_thenStatusOK() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(userId, 1, 1);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNotValidParamFrom_thenStatusBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNotValidParamSize0_thenStatusBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNotValidParamSizeNegative_thenStatusBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequests_withoutParams_thenStatusOk() {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(userId, defaultFrom, defaultSize);
    }

}