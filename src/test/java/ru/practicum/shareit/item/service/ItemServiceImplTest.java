package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    ItemServiceImpl itemServiceImpl;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    static ItemMapper itemMapper;
    static CommentMapper commentMapper;
    static BookingMapper bookingMapper;

    @BeforeAll
    static void beforeAll() {
        itemMapper = new ItemMapperImpl();
        commentMapper = new CommentMapperImpl();
        bookingMapper = new BookingMapperImpl();
    }

    @BeforeEach
    void beforeEach() {
        itemServiceImpl = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository, itemMapper, commentMapper, bookingMapper);
    }

    @Test
    void save() {
        Long userId = 1L;

        User user = makeUser(userId, "user", "user@user.com");

        ItemDto itemDto = new ItemDto(null, "Отвертка", "Аккумуляторная отвертка", true,
                null, 1L);

        ItemRequest itemRequest = makeItemRequest(1L, null, null, null);

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(userId, "user", "user@user.com")));

        Mockito
                .when(itemRequestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.of(itemRequest));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(makeItem(1L, "Отвертка", "Аккумуляторная отвертка", true, user, itemRequest));

        ItemDto saveItemDto = itemServiceImpl.save(userId, itemDto);

        assertThat(saveItemDto.getId(), equalTo(1L));
        assertThat(saveItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(saveItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(saveItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(saveItemDto.getOwnerId(), equalTo(user.getId()));
        assertThat(saveItemDto.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void saveComment() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = makeUser(userId, "user", "user@user.com");
        CommentDto commentDto = new CommentDto(1L, "Comment for item 1", user.getName(), LocalDateTime.now());
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, user, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findFirst1ByItemAndBookerAndEndBeforeOrderByEndDesc(Mockito.any(Item.class),
                        Mockito.any(User.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));

        Mockito
                .when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(makeComment(1L, commentDto.getText(), item, user, commentDto.getCreated()));

        CommentDto saveCommentDto = itemServiceImpl.saveComment(itemId, userId, commentDto);

        assertThat(saveCommentDto.getId(), equalTo(1L));
        assertThat(saveCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(saveCommentDto.getAuthorName(), equalTo(user.getName()));
        assertThat(saveCommentDto.getCreated(), notNullValue());
    }

    @Test
    void patchUpdate() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Отвертка", "Аккумуляторная отвертка", false, null, null);

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        User user = makeUser(userId, "user", "user@user.com");
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, user, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto patchItemDto = itemServiceImpl.patchUpdate(itemId, userId, itemDto);

        assertThat(patchItemDto.getId(), equalTo(itemId));
        assertThat(patchItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(patchItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(patchItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(patchItemDto.getOwnerId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void patchUpdateWhenItemDtoFieldsIsNull() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, null, null, null, null, null);

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        User user = makeUser(userId, "user", "user@user.com");
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, user, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto patchItemDto = itemServiceImpl.patchUpdate(itemId, userId, itemDto);

        assertThat(patchItemDto.getId(), equalTo(itemId));
        assertThat(patchItemDto.getName(), is(notNullValue()));
        assertThat(patchItemDto.getDescription(), is(notNullValue()));
        assertThat(patchItemDto.getAvailable(), is(notNullValue()));
        assertThat(patchItemDto.getOwnerId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void patchUpdateWhenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Отвертка", "Аккумуляторная отвертка", false, null, null);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        User user = makeUser(userId, "user", "user@user.com");
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, user, null);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceImpl.patchUpdate(itemId, 2L, itemDto));

        Assertions.assertEquals("вещь с id{" + itemId + "} не является вещью пользователя с id{" + 2L + "}",
                exception.getMessage());
    }

    @Test
    void findByIdWhenItemOwnerNotUserId() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        User owner = makeUser(2L, "owner", "owner@user.com");
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, owner, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findByItemOrderByCreatedDesc(Mockito.any(Item.class)))
                .thenReturn(Collections.emptyList());

        ItemDtoWithLastAndNextBooking findItem = itemServiceImpl.findById(itemId, userId);

        assertThat(findItem.getId(), equalTo(itemId));
        assertThat(findItem.getName(), equalTo(item.getName()));
        assertThat(findItem.getDescription(), equalTo(item.getDescription()));
        assertThat(findItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(findItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(findItem.getLastBooking(), nullValue());
        assertThat(findItem.getNextBooking(), nullValue());
        assertThat(findItem.getComments(), hasSize(0));
    }

    @Test
    void findById() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        User owner = makeUser(userId, "owner", "owner@user.com");
        Item item = makeItem(itemId, "Дрель", "Простая дрель", true, owner, null);

        Mockito
                .when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findByItemOrderByCreatedDesc(Mockito.any(Item.class)))
                .thenReturn(Collections.emptyList());

        ItemDtoWithLastAndNextBooking findItem = itemServiceImpl.findById(itemId, userId);

        assertThat(findItem.getId(), equalTo(itemId));
        assertThat(findItem.getName(), equalTo(item.getName()));
        assertThat(findItem.getDescription(), equalTo(item.getDescription()));
        assertThat(findItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(findItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(findItem.getLastBooking(), nullValue());
        assertThat(findItem.getNextBooking(), nullValue());
        assertThat(findItem.getComments(), hasSize(0));
    }

    @Test
    void findAllOwnerItemWhenBadRequestException() {
        Long ownerId = 1L;
        Integer from = -10;
        Integer size = 10;

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemServiceImpl.findAllOwnerItem(ownerId, from, size));

        Assertions.assertEquals("request param from{" + from + "} не может быть отрицательным",
                exception.getMessage());
    }

    @Test
    void findAllOwnerItem() {
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        int page = from / size;

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        List<Item> sourceItem = List.of(makeItem(1L, "Дрель", "Простая дрель", true, owner, null));

        Mockito
                .when(itemRepository.findByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(getItem(sourceItem, page, size));

        List<ItemDtoWithLastAndNextBooking> items = itemServiceImpl.findAllOwnerItem(ownerId, from, size);

        assertThat(items, hasSize(1));
        for (Item item : sourceItem) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", equalTo(item.getId())),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwner().getId()))
            )));
        }
    }

    @Test
    void findByNameAndDescriptionWhenBadRequestException() {
        String searchLine = "дрель";
        Boolean available = true;
        Integer from = -10;
        Integer size = 10;

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemServiceImpl.findByNameAndDescription(searchLine, available, from, size));

        Assertions.assertEquals("request param from{" + from + "} не может быть отрицательным",
                exception.getMessage());
    }

    @Test
    void findByNameAndDescriptionWhenSearchLineEmpty() {
        Long ownerId = 1L;
        String searchLine = "";
        Boolean available = true;
        Integer from = 0;
        Integer size = 10;

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        List<Item> sourceItem = List.of(makeItem(1L, "Дрель", "Простая дрель", true, owner,
                null));

        List<ItemDto> items = itemServiceImpl.findByNameAndDescription(searchLine, available, from, size);

        assertThat(items, hasSize(0));
    }

    @Test
    void findByNameAndDescription() {
        Long ownerId = 1L;
        String searchLine = "дрель";
        Boolean available = true;
        Integer from = 0;
        Integer size = 10;
        int page = from / size;

        User owner = makeUser(ownerId, "owner", "owner@user.com");
        List<Item> sourceItem = List.of(makeItem(1L, "Дрель", "Простая дрель", true, owner,
                null));

        Mockito
                .when(itemRepository.findSearchLineInNameAndDescription(Mockito.anyString(), Mockito.anyBoolean(),
                        Mockito.any(Pageable.class)))
                .thenReturn(getItem(sourceItem, page, size));

        List<ItemDto> items = itemServiceImpl.findByNameAndDescription(searchLine, available, from, size);

        assertThat(items, hasSize(1));
        for (Item item : sourceItem) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", equalTo(item.getId())),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwner().getId()))
            )));
        }
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private ItemRequest makeItemRequest(Long id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        return itemRequest;
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

    private Comment makeComment(Long id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }

    private Page<Item> getItem(List<Item> sourceItem, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), sourceItem.size());

        List<Item> pageContent = sourceItem.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, sourceItem.size());
    }

}