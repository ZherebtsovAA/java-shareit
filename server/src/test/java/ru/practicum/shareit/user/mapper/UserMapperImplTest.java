package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserMapperImplTest {
    private UserMapperImpl userMapper;

    @BeforeEach
    void beforeEach() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toUserDto() {
        User user = null;

        assertThat(userMapper.toUserDto(user), equalTo(null));
    }

    @Test
    void testToUserDto() {
        List<User> users = null;

        assertThat(userMapper.toUserDto(users), equalTo(null));
    }

    @Test
    void toUser() {
        UserDto userDto = null;

        assertThat(userMapper.toUser(userDto), equalTo(null));
    }
}