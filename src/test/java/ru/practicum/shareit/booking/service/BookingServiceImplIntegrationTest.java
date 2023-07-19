package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private static BookingMapper bookingMapper;
    private static UserMapper userMapper;
    private static ItemMapper itemMapper;
    private BookingServiceImpl bookingServiceImpl;

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
    void saveBooking() {
        Exception exception;

        User ownerItem = makeUser(null, "owner", "owner@ya.ru");
        em.persist(ownerItem);
        em.flush();

        User booker = makeUser(null, "booker", "booker@ya.ru");
        em.persist(booker);
        em.flush();

        Item sourceItem = makeItem(null, "дрель", "обычная дрель", false, ownerItem, null);
        em.persist(sourceItem);
        em.flush();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Long itemNotFound = sourceItem.getId() + 9999L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemNotFound);

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.save(booker.getId(), bookingRequestDto));

        Assertions.assertEquals("вещи с id{" + itemNotFound + "} нет в списке вещей", exception.getMessage());

        bookingRequestDto.setItemId(sourceItem.getId());

        exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.save(booker.getId(), bookingRequestDto));

        Assertions.assertEquals("вещь с id{" + sourceItem.getId() + "} не доступна для бронирования", exception.getMessage());

        bookingRequestDto.setItemId(sourceItem.getId());
        sourceItem.setAvailable(true);
        em.persist(sourceItem);
        em.flush();

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.save(ownerItem.getId(), bookingRequestDto));

        Assertions.assertEquals("бронирование владельцем своих же вещей не доступно", exception.getMessage());

        Long bookerNotFound = booker.getId() + 9999L;
        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.save(bookerNotFound, bookingRequestDto));

        Assertions.assertEquals("пользователя с id{" + bookerNotFound + "} нет в списке пользователей", exception.getMessage());


        BookingDto bookingDto = bookingServiceImpl.save(booker.getId(), bookingRequestDto);

        assertThat(bookingDto, hasProperty("id", is(notNullValue())));
        assertThat(bookingDto, hasProperty("start", is(equalTo(bookingRequestDto.getStart()))));
        assertThat(bookingDto, hasProperty("end", is(equalTo(bookingRequestDto.getEnd()))));
        assertThat(bookingDto, hasProperty("item", is(equalTo(itemMapper.toItemDto(sourceItem)))));
        assertThat(bookingDto, hasProperty("booker", is(equalTo(userMapper.toUserDto(booker)))));
        assertThat(bookingDto, hasProperty("status", is(equalTo(BookingStatus.WAITING))));
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
}