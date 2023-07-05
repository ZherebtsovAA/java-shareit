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
    public BookingDto create(@NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.save(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchUpdate(@PathVariable("bookingId") @Min(0) Long bookingId,
                                  @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @NotNull @RequestParam(value = "approved") Boolean approved) {
        return bookingService.patchUpdate(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable("bookingId") @Min(0) Long bookingId,
                               @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL",
                                                           required = false) BookingState state) {
        return bookingService.findAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingForAllItemByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @NotNull @RequestParam(value = "state", defaultValue = "ALL",
                                                                  required = false) BookingState state) {
        return bookingService.findBookingForAllItemByUserId(userId, state);
    }

}