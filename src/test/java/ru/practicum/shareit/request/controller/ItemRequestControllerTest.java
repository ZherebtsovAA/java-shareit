package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "description", 1L, null);

        Mockito
                .when(itemRequestService.save(Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequest = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    itemRequest.setId(1L);
                    itemRequest.setCreated(LocalDateTime.now());

                    return itemRequest;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        ItemRequestResponseDto itemRequest = new ItemRequestResponseDto(1L, "description", 1L,
                null, null);

        Mockito
                .when(itemRequestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findYourRequests() throws Exception {
        ItemRequestResponseDto itemRequest = new ItemRequestResponseDto(1L, "description", 1L,
                null, null);

        Mockito
                .when(itemRequestService.findYourRequests(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findOtherRequests() throws Exception {
        ItemRequestResponseDto itemRequest = new ItemRequestResponseDto(1L, "description", 1L,
                null, null);

        Mockito
                .when(itemRequestService.findOtherRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}