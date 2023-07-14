package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;
    private Long itemId;
    private Long userId;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .name("Name")
                .description("desc")
                .available(true).build();
        userId = 0L;
        itemId = 0L;
    }

    @SneakyThrows
    @Test
    void createItem_whenInvoke_thenStatusCreateItemDtoInBody() {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is2xxSuccessful());

        verify(itemClient).createItem(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenNotHeadUserId_thenStatusBadRequest() {
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void createItem_whenNotBody_thenStatusBadRequest() {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void createItem_whenNotValidItemName_thenStatusBadRequest(String input) {
        itemDto.setName(input);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void createItem_whenNotValidItemDesc_thenStatusBadRequest(String input) {
        itemDto.setDescription(input);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void createItem_whenNotValidItemAvailable_thenStatusBadRequest() {
        itemDto.setAvailable(null);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -15, Long.MIN_VALUE})
    @SneakyThrows
    void createItem_whenNotValidItemRequestId_thenStatusBadRequest(Long number) {
        itemDto.setRequestId(number);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void update_whenInvoke_thenStatusOk() {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient).update(userId, itemId, itemDto);
    }

    @SneakyThrows
    @Test
    void update_whenNotHeadUserId_thenStatusBadRequest() {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void update_whenEmptyBody_thenStatusBadRequest() {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void update_whenResponseStatusNotFound_thenStatusNotFound() {
        when(itemClient.update(any(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getByItemId_whenInvoke_thenStatusOk() {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());

        verify(itemClient).getByItemId(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getByItemId_whenResponseStatusNotFound_thenStatusNotFound() {
        when(itemClient.getByItemId(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getByItemId_whenNotHeadUserId_thenStatusBadRequest() {
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserId_whenInvoke_thenStatusOk() {
        Integer from = 1;
        Integer size = 1;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk());

        verify(itemClient).getAllItemsDyUserId(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserId_whenNotHeadUserId_thenStatusBadRequest() {
        Integer from = 1;
        Integer size = 1;

        mockMvc.perform(get("/items")
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItemsDyUserId(anyLong(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserId_whenResponseStatusNotFound_thenStatusNotFound() {
        Integer from = 1;
        Integer size = 1;
        when(itemClient.getAllItemsDyUserId(anyLong(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void getAllItemsByUserId_whenNotValidParamFrom_thenStatusBadRequest(Integer from) {
        Integer size = 1;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void getAllItemsByUserId_whenNotValidParamSize_thenStatusBadRequest(Integer size) {
        Integer from = 1;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenStatusOk() {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).deleteItem(userId, itemId);

    }

    @SneakyThrows
    @Test
    void delete_whenResponseStatusNotFound_thenStatusNotFound() {
        when(itemClient.deleteItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }


    @SneakyThrows
    @Test
    void search_whenInvoke_thenStatusOk() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk());

        verify(itemClient).search(userId, text, from, size);
    }

    @SneakyThrows
    @Test
    void search_whenNotHeadUserId_thenStatusBadRequest() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void search_whenResponseStatusNotFound_thenStatusNotFound() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        when(itemClient.search(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void search_whenNotValidParamFrom_thenStatusBadRequest(Integer from) {
        String text = "text";
        Integer size = 1;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void search_whenNotValidParamSize_thenStatusBadRequest(Integer size) {
        String text = "text";
        Integer from = 1;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void search_whenNotParamText_thenStatusBadRequest() {
        Integer size = 1;
        Integer from = 1;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getByItemId(anyLong(), anyLong());
    }


    @SneakyThrows
    @Test
    void createComment_whenInvoke_thenStatusCreateItemDtoInBody() {
        CommentDto commentDto = CommentDto.builder().text("test").build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().is2xxSuccessful());

        verify(itemClient).createComment(userId, itemId, commentDto);
    }

    @SneakyThrows
    @Test
    void createComment_whenNotHeadUserId_thenStatusBadRequest() {
        CommentDto commentDto = CommentDto.builder().text("test").build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createComment_whenNotBody_thenStatusBadRequest() {
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(any(), any(), any());
    }

    @ParameterizedTest
    @SneakyThrows
    @NullAndEmptySource
    void createComment_whenNotValidCommentText_thenStatusBadRequest(String input) {
        CommentDto commentDto = CommentDto.builder().text(input).build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(), any());
    }

    @SneakyThrows
    @Test
    void createComment_whenResponseStatusNotFound_thenStatusNotFound() {
        CommentDto commentDto = CommentDto.builder().text("input").build();
        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }
}