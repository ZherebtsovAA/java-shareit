package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long bookerId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.save(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchUpdate(@PathVariable("bookingId") @Min(0) Long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                  @RequestParam(value = "approved") @NotNull Boolean approved) {
        return bookingService.patchUpdate(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable("bookingId") @Min(0) Long bookingId,
                               @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return bookingService.findAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingForAllItemByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return bookingService.findBookingForAllItemByUserId(userId, state, from, size);
    }

}