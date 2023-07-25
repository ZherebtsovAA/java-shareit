package ru.practicum.shareit.user.service;

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
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository repository;
    private static UserMapper userMapper;

    @BeforeAll
    static void beforeAll() {
        userMapper = new UserMapperImpl();
    }

    @BeforeEach
    void beforeEach() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);
    }

    @Test
    void save() {
        UserDto userDto = new UserDto(null, "user", "user@user.com");

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);
                    user.setId(1L);

                    return user;
                });

        UserDto userCreated = userServiceImpl.save(userDto);

        assertThat(userCreated.getId(), notNullValue());
        assertThat(userCreated.getName(), equalTo(userDto.getName()));
        assertThat(userCreated.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void patchUpdateWhenNotFoundException() {
        Long userNotFound = 9999L;
        UserDto userDto = new UserDto();

        Mockito
                .when(repository.findById(userNotFound))
                .thenThrow(new NotFoundException("пользователя с id{" + userNotFound + "} нет в списке пользователей"));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userServiceImpl.patchUpdate(userNotFound, userDto));

        Assertions.assertEquals("пользователя с id{" + userNotFound + "} нет в списке пользователей",
                exception.getMessage());
    }

    @Test
    void patchUpdateWhenConflictException() {
        Long userId = 2L;
        UserDto userDto = new UserDto();
        userDto.setName("userUpdate");
        userDto.setEmail("user@user.com");

        Mockito
                .when(repository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName("user2");
                    user.setEmail("user@user.com");

                    return Optional.of(user);
                });

        Mockito
                .when(repository.findByEmail(userDto.getEmail()))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(1L);
                    user.setName("user");
                    user.setEmail("user@user.com");

                    return Optional.of(user);
                });

        ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userServiceImpl.patchUpdate(userId, userDto));

        Assertions.assertEquals("email{" + userDto.getEmail() + "} уже используется", exception.getMessage());
    }

    @Test
    void patchUpdate() {
        Long userId = 2L;
        UserDto userDto = new UserDto();
        userDto.setName("userUpdate");
        userDto.setEmail("userUpdate@user.com");

        Mockito
                .when(repository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName("user2");
                    user.setEmail("user2@user.com");

                    return Optional.of(user);
                });

        Mockito
                .when(repository.findByEmail(userDto.getEmail()))
                .thenReturn(Optional.empty());

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto updateUserDto = userServiceImpl.patchUpdate(userId, userDto);

        assertThat(updateUserDto.getId(), equalTo(userId));
        assertThat(updateUserDto.getName(), equalTo(userDto.getName()));
        assertThat(updateUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void patchUpdateWhenUserDtoFieldsIsNull() {
        Long userId = 2L;
        UserDto userDto = new UserDto(null, null, null);

        Mockito
                .when(repository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName("findUser");
                    user.setEmail("findUser@user.com");

                    return Optional.of(user);
                });

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto updateUserDto = userServiceImpl.patchUpdate(userId, userDto);

        assertThat(updateUserDto.getId(), equalTo(userId));
        assertThat(updateUserDto.getName(), is(notNullValue()));
        assertThat(updateUserDto.getEmail(), is(notNullValue()));
    }

    @Test
    void findById() {
        Long userId = 1L;

        Mockito
                .when(repository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName("user");
                    user.setEmail("user@user.com");

                    return Optional.of(user);
                });

        userServiceImpl.findById(userId);

        Mockito.verify(repository, Mockito.times(1))
                .findById(userId);
    }

    @Test
    void findAll() {
        int numberPage = 0;
        int numberUserToView = 10;

        Mockito
                .when(repository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(getUser(List.of(makeUser(1L, "user", "user@ya.ru")), numberPage, numberUserToView));

        List<UserDto> users = userServiceImpl.findAll(numberPage, numberUserToView);

        assertThat(users, hasSize(1));

        Mockito.verify(repository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    void deleteById() {
        Long userId = 1L;

        Mockito
                .when(repository.findById(userId))
                .thenReturn(Optional.of(new User()));

        userServiceImpl.deleteById(userId);

        Mockito.verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Page<User> getUser(List<User> sourceUser, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), sourceUser.size());

        List<User> pageContent = sourceUser.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, sourceUser.size());
    }
}