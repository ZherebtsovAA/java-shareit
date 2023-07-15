package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserModelTest {

    @Test
    void testEquals() {
        User first = makeUser(1L, "first", "first@email.ru");
        User second = makeUser(2L, "second", "second@email.ru");

        assertFalse(first.equals(second));
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

}