package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class BookingMapperImplTest {
    private BookingMapperImpl bookingMapper;

    @BeforeEach
    void beforeEach() {
        bookingMapper = new BookingMapperImpl();
    }

    @Test
    void toBookingDto() {
        Booking booking = null;

        assertThat(bookingMapper.toBookingDto(booking, null, null), equalTo(null));
    }

    @Test
    void testToBookingDto() {
        List<Booking> bookings = null;

        assertThat(bookingMapper.toBookingDto(bookings), equalTo(null));
    }

    @Test
    void toBooking() {
        BookingRequestDto booking = null;

        assertThat(bookingMapper.toBooking(booking, null, null, null), equalTo(null));
    }

    @Test
    void toBookingResponseDto() {
        Booking booking = null;

        assertThat(bookingMapper.toBookingResponseDto(booking), equalTo(null));

        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        booking = makeBooking(bookingId, start, end, sourceItem, booker, BookingStatus.WAITING);

        assertThat(bookingMapper.toBookingResponseDto(booking), notNullValue());
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item makeItem(Long id, String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
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