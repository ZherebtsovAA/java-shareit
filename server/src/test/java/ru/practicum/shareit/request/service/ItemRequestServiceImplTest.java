package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private ItemRequestServiceImpl itemRequestServiceImpl;
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private static ItemRequestMapper itemRequestMapper;
    private static ItemMapper itemMapper;

    @BeforeAll
    static void beforeAll() {
        itemRequestMapper = new ItemRequestMapperImpl();
        itemMapper = new ItemMapperImpl();
    }

    @BeforeEach
    void beforeEach() {
        itemRequestServiceImpl = new ItemRequestServiceImpl(repository, userRepository, itemRepository,
                itemRequestMapper, itemMapper);
    }

    @Test
    void save() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");

        Mockito
                .when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName("user");
                    user.setEmail("user@user.com");

                    return Optional.of(user);
                });

        Mockito
                .when(repository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequest itemRequest = invocationOnMock.getArgument(0, ItemRequest.class);
                    itemRequest.setId(1L);

                    return itemRequest;
                });


        ItemRequestDto saveItemRequest = itemRequestServiceImpl.save(userId, itemRequestDto);

        assertThat(saveItemRequest.getId(), equalTo(1L));
        assertThat(saveItemRequest.getDescription(), equalTo("Хотел бы воспользоваться щёткой для обуви"));
        assertThat(saveItemRequest.getCreated(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(saveItemRequest.getRequestorId(), equalTo(userId));
    }

    @Test
    void saveWhenNotFoundException() {
        Long userNotFound = 9999L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");

        Mockito
                .when(userRepository.findById(userNotFound))
                .thenThrow(new NotFoundException("пользователя с id{" + userNotFound + "} нет в списке пользователей"));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestServiceImpl.save(userNotFound, itemRequestDto));

        Assertions.assertEquals("пользователя с id{" + userNotFound + "} нет в списке пользователей", exception.getMessage());
    }

    @Test
    void findById() {
        Long requestId = 1L;
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(null, null, null)));

        Mockito
                .when(repository.findById(requestId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(2L);

                    ItemRequest itemRequest = new ItemRequest();
                    itemRequest.setId(requestId);
                    itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
                    itemRequest.setRequestor(user);
                    itemRequest.setCreated(LocalDateTime.now());

                    return Optional.of(itemRequest);
                });

        Mockito
                .when(itemRepository.findAllItemByRequestId(Mockito.any()))
                .thenReturn(Collections.emptyList());


        ItemRequestResponseDto itemRequestResponseDto = itemRequestServiceImpl.findById(requestId, userId);

        assertThat(itemRequestResponseDto.getId(), equalTo(requestId));
        assertThat(itemRequestResponseDto.getDescription(), equalTo("Хотел бы воспользоваться щёткой для обуви"));
        assertThat(itemRequestResponseDto.getRequestorId(), equalTo(2L));
        assertThat(itemRequestResponseDto.getCreated(), notNullValue());
        assertThat(itemRequestResponseDto.getItems(), is(Collections.emptyList()));
    }

    @Test
    void findByIdWhenNotFoundException() {
        Long requestNotFound = 9999L;
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(makeUser(0L, "user", "email@ya.ru")));

        Mockito
                .when(repository.findById(requestNotFound))
                .thenThrow(new NotFoundException("запроса с id{" + requestNotFound + "} нет в списке запросов на создание вещей"));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestServiceImpl.findById(requestNotFound, userId));

        Assertions.assertEquals("запроса с id{" + requestNotFound + "} нет в списке запросов на создание вещей", exception.getMessage());
    }

    @Test
    void findYourRequests() {
        Long userId = 1L;

        List<ItemRequest> sourceItemRequest = List.of(
                makeItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви",
                        makeUser(userId, "user", "user@user.com"), LocalDateTime.now()));

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(sourceItemRequest.get(0).getRequestor()));

        Mockito
                .when(repository.findByRequestorOrderByCreatedDesc(Mockito.any(User.class)))
                .thenReturn(sourceItemRequest);

        Mockito
                .when(itemRepository.findAllItemByRequestId(Mockito.any()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> items = itemRequestServiceImpl.findYourRequests(userId);

        assertThat(items, hasSize(1));

        for (ItemRequest itemRequest : sourceItemRequest) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequest.getDescription())),
                    hasProperty("requestorId", equalTo(itemRequest.getRequestor().getId())),
                    hasProperty("created", notNullValue())
            )));
        }

    }

    @Test
    void findYourRequestsWhenNotFoundException() {
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(userId))
                .thenThrow(new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestServiceImpl.findYourRequests(userId));

        Assertions.assertEquals("пользователя с id{" + userId + "} нет в списке пользователей", exception.getMessage());
    }

    @Test
    void findOtherRequests() {
        Long userId = 1L;
        Integer from = 1;
        Integer size = 10;
        int page = from / size;

        User user = makeUser(userId, "user", "user@user.com");
        User otherUser = makeUser(9L, "user", "user@user.com");

        List<ItemRequest> sourceItemRequest = List.of(
                makeItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви", otherUser, LocalDateTime.now()));

        Mockito
                .when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Mockito
                .when(repository.findByRequestorNotOrderByCreatedDesc(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(getItemRequest(sourceItemRequest, page, size));

        Mockito
                .when(itemRepository.findAllItemByRequestId(Mockito.any()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> items = itemRequestServiceImpl.findOtherRequests(userId, from, size);

        assertThat(items, hasSize(1));
        for (ItemRequest itemRequest : sourceItemRequest) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequest.getDescription())),
                    hasProperty("requestorId", equalTo(itemRequest.getRequestor().getId())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    private Page<ItemRequest> getItemRequest(List<ItemRequest> sourceItemRequest, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), sourceItemRequest.size());

        List<ItemRequest> pageContent = sourceItemRequest.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, sourceItemRequest.size());
    }

    private ItemRequest makeItemRequest(Long id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        return itemRequest;
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}