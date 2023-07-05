package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface BookingService {

    BookingDto save(Long bookerId, BookingRequestDto bookingRequestDto) throws NotFoundException, BadRequestException;

    BookingDto patchUpdate(Long bookingId, Long ownerId, Boolean approved) throws NotFoundException, BadRequestException;

    BookingDto findById(Long bookingId, Long userId) throws NotFoundException;

    List<BookingDto> findAllBookingByUserId(Long userId, BookingState state) throws NotFoundException;

    List<BookingDto> findBookingForAllItemByUserId(Long userId, BookingState state) throws NotFoundException;

}