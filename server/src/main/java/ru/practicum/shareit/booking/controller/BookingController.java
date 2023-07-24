package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long bookerId,
                             @RequestBody BookingRequestDto bookingRequestDto,
                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), bookerId, bookingRequestDto);

        return bookingService.save(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchUpdate(@PathVariable Long bookingId,
                                  @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                  @RequestParam(value = "approved") Boolean approved,
                                  HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), ownerId);

        return bookingService.patchUpdate(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader(X_SHARER_USER_ID) Long userId,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingByUserId(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return bookingService.findAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingForAllItemByUserId(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return bookingService.findBookingForAllItemByUserId(userId, state, from, size);
    }
}