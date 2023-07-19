package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService itemService;
    private static ItemMapper itemMapper;

    @BeforeAll
    static void beforeAll() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void patchUpdate() {
        Exception exception;

        User owner = makeUser("owner", "owner@ya.ru");
        em.persist(owner);
        em.flush();

        User otherUser = makeUser("other", "other@ya.ru");
        em.persist(otherUser);
        em.flush();

        Item sourceItem = makeItem("дрель", "обычная дрель", true, owner, null);
        em.persist(sourceItem);
        em.flush();

        Long userNotFound = owner.getId() + 9999L;

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchUpdate(sourceItem.getId(), userNotFound, itemMapper.toItemDto(sourceItem)));

        Assertions.assertEquals("пользователя с id{" + userNotFound + "} нет в списке пользователей", exception.getMessage());

        long itemNotFound = sourceItem.getId() + 9999L;

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchUpdate(itemNotFound, owner.getId(), itemMapper.toItemDto(sourceItem)));

        Assertions.assertEquals("вещи с id{" + itemNotFound + "} нет в списке вещей", exception.getMessage());

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchUpdate(sourceItem.getId(), otherUser.getId(), itemMapper.toItemDto(sourceItem)));

        Assertions.assertEquals("вещь с id{" + sourceItem.getId() + "} не является вещью пользователя с id{"
                + otherUser.getId() + "}", exception.getMessage());

        ItemDto itemDto = new ItemDto();
        itemDto.setName("пила");
        itemDto.setDescription("бензопила, 92 бензин");
        itemDto.setAvailable(false);

        ItemDto itemUpdate = itemService.patchUpdate(sourceItem.getId(), owner.getId(), itemDto);

        assertThat(itemUpdate, hasProperty("id", is(equalTo(sourceItem.getId()))));
        assertThat(itemUpdate, hasProperty("name", equalTo("пила")));
        assertThat(itemUpdate, hasProperty("description", equalTo("бензопила, 92 бензин")));
        assertThat(itemUpdate, hasProperty("available", equalTo(false)));
        assertThat(itemUpdate, hasProperty("ownerId", is(equalTo(sourceItem.getOwner().getId()))));
        assertThat(itemUpdate, hasProperty("requestId", is(equalTo(sourceItem.getRequest()))));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item makeItem(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }
}