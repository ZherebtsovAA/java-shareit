package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusDays(1);
        ItemDto item = new ItemDto(1L, "name", "description", true, 1L, null);
        UserDto booker = new UserDto(1L, "name", "name@email");
        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, 1L);

        Mockito
                .when(bookingService.save(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void patchUpdate() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusDays(1);
        ItemDto item = new ItemDto(1L, "name", "description", true, 1L, null);
        UserDto booker = new UserDto(1L, "name", "name@email");
        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.WAITING);

        Mockito
                .when(bookingService.patchUpdate(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    Boolean approved = invocationOnMock.getArgument(2, Boolean.class);
                    if (approved) {
                        bookingDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingDto.setStatus(BookingStatus.REJECTED);
                    }

                    return bookingDto;
                });

        mvc.perform(patch("/bookings/{bookingId}?approved=false", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusDays(1);
        ItemDto item = new ItemDto(1L, "name", "description", true, 1L, null);
        UserDto booker = new UserDto(1L, "name", "name@email");
        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.APPROVED);

        Mockito
                .when(bookingService.findById(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAllBookingByUserId() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusDays(1);
        ItemDto item = new ItemDto(1L, "name", "description", true, 1L, null);
        UserDto booker = new UserDto(1L, "name", "name@email");
        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.APPROVED);

        Mockito
                .when(bookingService.findAllBookingByUserId(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findBookingForAllItemByUserId() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = start.plusDays(1);
        ItemDto item = new ItemDto(1L, "name", "description", true, 1L, null);
        UserDto booker = new UserDto(1L, "name", "name@email");
        BookingDto bookingDto = new BookingDto(1L, start, end, item, booker, BookingStatus.APPROVED);

        Mockito
                .when(bookingService.findAllBookingByUserId(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}