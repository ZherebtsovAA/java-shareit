package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemDto itemDto = new ItemDto(null, "name", "description", true, null, null);

        Mockito
                .when(itemService.save(Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto item = invocationOnMock.getArgument(1, ItemDto.class);
                    item.setId(1L);
                    item.setOwnerId(1L);

                    return item;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "text", "author", null);

        Mockito
                .when(itemService.saveComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    CommentDto comment = invocationOnMock.getArgument(2, CommentDto.class);
                    comment.setId(1L);

                    return comment;
                });

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void patchUpdate() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L, null);

        Mockito
                .when(itemService.patchUpdate(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto item = invocationOnMock.getArgument(2, ItemDto.class);
                    item.setId(1L);
                    item.setOwnerId(1L);

                    return item;
                });

        mvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        Mockito
                .when(itemService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ItemDtoWithLastAndNextBooking());

        mvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllOwnerItem() throws Exception {
        Mockito
                .when(itemService.findAllOwnerItem(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(new ItemDtoWithLastAndNextBooking()));

        mvc.perform(get("/items", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findByNameAndDescription() throws Exception {
        Mockito
                .when(itemService.findByNameAndDescription(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(new ItemDto()));

        mvc.perform(get("/items/search?text='qwerty'"))
                .andExpect(status().isOk());
    }

    @Test
    void handleThrowable() throws Exception {
        ItemDto itemDto = new ItemDto(null, "name", "description", true, null, null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}