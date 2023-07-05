package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    public abstract BookingDto toBookingDto(Booking booking, ItemDto item, UserDto booker);

    public abstract List<BookingDto> toBookingDto(Iterable<Booking> bookings);

    @Mapping(source = "bookingRequestDto.id", target = "id")
    @Mapping(source = "bookingRequestDto.start", target = "start")
    @Mapping(source = "bookingRequestDto.end", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "bookingStatus", target = "status")
    public abstract Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker, BookingStatus bookingStatus);

    @Mapping(source = "booking.booker.id", target = "bookerId")
    public abstract BookingResponseDto toBookingResponseDto(Booking booking);

}
