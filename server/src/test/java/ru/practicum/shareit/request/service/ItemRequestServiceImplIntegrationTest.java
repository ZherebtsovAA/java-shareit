package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void save() {
        User user = makeUser("user", "user@ya.ru");
        em.persist(user);
        em.flush();

        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "игра Монополия", user.getId(), null);

        long userNotFound = user.getId() + 9999L;
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.save(userNotFound, itemRequestDto));

        Assertions.assertEquals("пользователя с id{" + userNotFound + "} нет в списке пользователей", exception.getMessage());

        ItemRequestDto saveItemRequestDto = itemRequestService.save(user.getId(), itemRequestDto);

        assertThat(saveItemRequestDto, hasProperty("id", is(notNullValue())));
        assertThat(saveItemRequestDto, hasProperty("description", is(equalTo(itemRequestDto.getDescription()))));
        assertThat(saveItemRequestDto, hasProperty("requestorId", is(equalTo(itemRequestDto.getRequestorId()))));
        assertThat(saveItemRequestDto, hasProperty("created", is(notNullValue())));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}