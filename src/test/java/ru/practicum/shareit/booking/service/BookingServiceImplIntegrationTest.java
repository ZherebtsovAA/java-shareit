package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    @Autowired
    EntityManager em;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    static BookingMapper bookingMapper;
    static UserMapper userMapper;
    static ItemMapper itemMapper;
    BookingServiceImpl bookingServiceImpl;

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
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, start, end, itemId);

        User owner = makeUser(null, "owner", "owner@ya.ru");
        em.persist(owner);
        em.flush();

        User booker = makeUser(null, "booker", "booker@ya.ru");
        em.persist(booker);
        em.flush();

        Item sourceItem = makeItem(null, "дрель", "обычная дрель", true, owner, null);
        em.persist(sourceItem);
        em.flush();

        BookingDto bookingDto = bookingServiceImpl.save(booker.getId(), bookingRequestDto);

        assertThat(bookingDto, notNullValue());
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