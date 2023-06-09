package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ItemRequestService itemRequestService;
    private Long userId;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenInvokeItemRequestService() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("desc").build();
        when(itemRequestService.create(userId, itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void create_whenBodyNotValid_thenStatusBadRequestBadRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserNotFound_thenStatusNotFound() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("desc").build();
        when(itemRequestService.create(userId, itemRequestDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequestsByUser_whenInvoke_thenStatusOkAndListRequestsInBody() {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestDto.builder()
                .description("desc").build());
        when(itemRequestService.getAllRequestByUser(userId)).thenReturn(itemRequestDtoList);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenInvoke_thenStatusOkAndItemRequestsInBody() {
        Long requestId = 0L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("desc").build();
        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenItemRequestNotFound_thenStatusNotFound() {
        Long requestId = 0L;
        when(itemRequestService.getRequestById(userId, requestId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequests_withValidParams() {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestDto.builder()
                .description("desc").build());
        when(itemRequestService.getAllRequests(userId, 1, 1)).thenReturn(itemRequestDtoList);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @SneakyThrows
    @Test
    void getAllRequests_withNotValidParams_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).getAllRequests(userId, 1, 1);
    }
}