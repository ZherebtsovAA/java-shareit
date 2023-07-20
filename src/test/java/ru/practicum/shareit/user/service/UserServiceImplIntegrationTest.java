package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsIterableContaining.hasItem;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private UserService userService;

    @Test
    void patchWhenConflictException() {
        Long userId = userService.save(new UserDto(null, "name", "name@ya.ru")).getId();
        userService.save(new UserDto(null, "noName", "noName@ya.ru"));

        ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.patchUpdate(userId, new UserDto(null, "name", "noName@ya.ru")));
    }

    @Test
    void findAll() {
        List<User> sourceUsers = List.of(
                makeUser("user1", "user1@ya.ru"),
                makeUser("user2", "user2@ya.ru"),
                makeUser("user3", "user3@ya.ru"));

        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();

        List<UserDto> users = userService.findAll(0, 10);

        assertThat(users, hasSize(sourceUsers.size()));
        for (User user : sourceUsers) {
            assertThat(users, hasItem(allOf(
                    hasProperty("name", equalTo(user.getName()))
            )));
        }
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}