package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto save(Long bookerId, BookingRequestDto bookingRequestDto);

    BookingDto patchUpdate(Long bookingId, Long ownerId, Boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findAllBookingByUserId(Long userId, BookingState state, Integer from, Integer size);

    List<BookingDto> findBookingForAllItemByUserId(Long userId, BookingState state, Integer from, Integer size);
}