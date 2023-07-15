package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BookingModelTest {

    @Test
    void testEquals() {
        Booking first = makeBooking(1L, null, null, null, null, null);
        Booking second = makeBooking(2L, null, null, null, null, null);

        assertFalse(first.equals(second));
        assertNotEquals(first.hashCode(), second.hashCode());
    }

    private Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }

}