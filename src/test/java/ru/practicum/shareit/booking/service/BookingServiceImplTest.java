package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl bookingServiceImpl;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private static BookingMapper bookingMapper;
    private static UserMapper userMapper;
    private static ItemMapper itemMapper;

    @BeforeAll
    static void beforeAll() {
        bookingMapper = new BookingMapperImpl();
        userMapper = new UserMapperImpl();
        itemMapper = new ItemMapperImpl();
    }

    @BeforeEach
    void beforeEach() {
        bookingServiceImpl = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper,
                userMapper, itemMapper);
    }

    @Test
    void save() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);
        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(sourceItem));

        Mockito
                .when(bookingRepository.findByItemAndStatusIn(Mockito.any(Item.class), Mockito.anyList(), Mockito.any(Sort.class)))
                .thenReturn(Collections.emptyList());

        Mockito
                .when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = invocationOnMock.getArgument(0, Booking.class);
                    booking.setId(bookingId);
                    return booking;
                });

        BookingDto saveBookingDto = bookingServiceImpl.save(bookerId, bookingRequestDto);

        assertThat(saveBookingDto.getId(), equalTo(bookingId));
        assertThat(saveBookingDto.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(saveBookingDto.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(saveBookingDto.getItem(), notNullValue());
        assertThat(saveBookingDto.getBooker(), notNullValue());
        assertThat(saveBookingDto.getStatus(), notNullValue());
    }

    @Test
    void saveWhenBadRequestException() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);
        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        Booking sourceBooking = makeBooking(bookingId, start, end, sourceItem, booker, BookingStatus.WAITING);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(sourceItem));

        Mockito
                .when(bookingRepository.findByItemAndStatusIn(Mockito.any(Item.class), Mockito.anyList(), Mockito.any(Sort.class)))
                .thenReturn(List.of(sourceBooking));

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.save(bookerId, bookingRequestDto));

        Assertions.assertEquals("бронирование на указанные даты и время невозможно", exception.getMessage());
    }


    @Test
    void saveWhenNotFoundException() {
        Long bookerId = 1L;
        Long ownerId = 1L;
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);
        User owner = makeUser(ownerId, "owner", "owner@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(sourceItem));

        Mockito
                .when(bookingRepository.findByItemAndStatusIn(Mockito.any(Item.class), Mockito.anyList(), Mockito.any(Sort.class)))
                .thenReturn(Collections.emptyList());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.save(bookerId, bookingRequestDto));

        Assertions.assertEquals("бронирование владельцем своих же вещей не доступно", exception.getMessage());
    }

    @Test
    void saveWhenItemNotAvailable() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);
        User owner = makeUser(ownerId, "owner", "owner@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", false, owner, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(sourceItem));

        Mockito
                .when(bookingRepository.findByItemAndStatusIn(Mockito.any(Item.class), Mockito.anyList(), Mockito.any(Sort.class)))
                .thenReturn(Collections.emptyList());

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.save(bookerId, bookingRequestDto));

        Assertions.assertEquals("вещь с id{" + itemId + "} не доступна для бронирования", exception.getMessage());
    }

    @Test
    void patchUpdate() {
        boolean approved = true;
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        Booking sourceBooking = makeBooking(bookingId, start, end, sourceItem, booker, null);
        BookingDto patchBooking;

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(sourceBooking));

        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        patchBooking = bookingServiceImpl.patchUpdate(bookingId, ownerId, approved);

        assertThat(patchBooking.getStatus(), equalTo(BookingStatus.APPROVED));

        approved = false;
        sourceBooking.setStatus(null);
        patchBooking = bookingServiceImpl.patchUpdate(bookingId, ownerId, approved);

        assertThat(patchBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void patchUpdateException() {
        Boolean approved = true;
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        Booking sourceBooking = makeBooking(bookingId, start, end, sourceItem, booker, BookingStatus.APPROVED);

        RuntimeException exception;

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(sourceBooking));

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.patchUpdate(bookingId, 9L, approved));

        Assertions.assertEquals("подтверждение бронирования может выполнить только владелец вещи", exception.getMessage());

        exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.patchUpdate(bookingId, ownerId, approved));

        Assertions.assertEquals("подтверждение бронирования уже выполнено", exception.getMessage());
    }

    @Test
    void findByIdNotFoundException() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        Booking sourceBooking = makeBooking(bookingId, start, end, sourceItem, booker, null);

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(sourceBooking));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.findById(bookingId, 99L));

        Assertions.assertEquals("операция может быть выполнена либо автором бронирования, либо владельцем вещи",
                exception.getMessage());
    }

    @Test
    void findById() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        Booking sourceBooking = makeBooking(bookingId, start, end, sourceItem, booker, null);

        Mockito
                .when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(sourceBooking));

        BookingDto bookingDto = bookingServiceImpl.findById(bookingId, bookerId);

        assertThat(bookingDto, notNullValue());
    }

    @Test
    void findAllBookingByUserId() {
        Long bookerId = 9L;
        Long userId = 3L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Integer from = 0;
        Integer size = 10;
        int page = from / size;
        List<BookingDto> bookings;

        User user = makeUser(userId, "user", "user@user.com");
        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        List<Booking> sourceBooking = List.of(makeBooking(bookingId, start, end, sourceItem, booker, null));

        Mockito
                .when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(user));

        //ALL
        Mockito
                .when(bookingRepository.findByBooker(Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.ALL, from, size);

        assertThat(bookings, notNullValue());

        //CURRENT
        Mockito
                .when(bookingRepository.findByBookerWhereStatusCurrent(Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.CURRENT, from, size);

        assertThat(bookings, notNullValue());

        //PAST
        Mockito
                .when(bookingRepository.findByBookerAndEndBefore(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.PAST, from, size);

        assertThat(bookings, notNullValue());

        //FUTURE
        Mockito
                .when(bookingRepository.findByBookerAndStatusInAndStartAfter(Mockito.any(), Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.FUTURE, from, size);

        assertThat(bookings, notNullValue());

        //WAITING
        Mockito
                .when(bookingRepository.findByBookerAndStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.WAITING, from, size);

        assertThat(bookings, notNullValue());

        //REJECTED
        Mockito
                .when(bookingRepository.findByBookerAndStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findAllBookingByUserId(ownerId, BookingState.REJECTED, from, size);

        assertThat(bookings, notNullValue());
    }

    @Test
    void findBookingForAllItemByUserId() {
        Long bookerId = 9L;
        Long ownerId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Integer from = 0;
        Integer size = 10;
        int page = from / size;
        List<BookingDto> bookings;

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        User booker = makeUser(bookerId, "booker", "booker@user.com");
        Item sourceItem = makeItem(1L, "дрель", "обычная дрель", true, owner, null);
        List<Booking> sourceBooking = List.of(makeBooking(bookingId, start, end, sourceItem, booker, null));

        Mockito
                .when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        //ALL
        Mockito
                .when(bookingRepository.findBookingForAllItemByUser(Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.ALL, from, size);

        assertThat(bookings, notNullValue());

        //CURRENT
        Mockito
                .when(bookingRepository.findBookingAllItemByUserWhereStatusCurrent(Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.CURRENT, from, size);

        assertThat(bookings, notNullValue());

        //PAST
        Mockito
                .when(bookingRepository.findBookingForAllItemByUserWhereEndBefore(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.PAST, from, size);

        assertThat(bookings, notNullValue());

        //FUTURE
        Mockito
                .when(bookingRepository.findBookingForAllItemByUserWhereStartAfter(Mockito.any(), Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.FUTURE, from, size);

        assertThat(bookings, notNullValue());

        //WAITING
        Mockito
                .when(bookingRepository.findBookingForAllItemByUser(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.WAITING, from, size);

        assertThat(bookings, notNullValue());

        //REJECTED
        Mockito
                .when(bookingRepository.findBookingForAllItemByUser(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(getBooking(sourceBooking, page, size));

        bookings = bookingServiceImpl.findBookingForAllItemByUserId(ownerId, BookingState.REJECTED, from, size);

        assertThat(bookings, notNullValue());
    }

    @Test
    void checkStartAndEndDateBookingWhenBadRequestException() {
        Long bookerId = 9L;
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.save(bookerId, bookingRequestDto));

        Assertions.assertEquals("дата окончания бронирования ранее даты начала бронирования",
                exception.getMessage());

        bookingRequestDto.setEnd(start);
        exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.save(bookerId, bookingRequestDto));

        Assertions.assertEquals("дата окончания бронирования равна дате начала бронирования",
                exception.getMessage());
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

    private Page<Booking> getBooking(List<Booking> sourceBooking, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), sourceBooking.size());

        List<Booking> pageContent = sourceBooking.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, sourceBooking.size());
    }

}