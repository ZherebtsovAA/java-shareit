package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void patchWhenConflictException() {
        userService.save(new UserDto(null, "name", "name@ya.ru"));
        userService.save(new UserDto(null, "noName", "noName@ya.ru"));

        ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.patchUpdate(1L, new UserDto(null, "name", "noName@ya.ru")));

    }

}