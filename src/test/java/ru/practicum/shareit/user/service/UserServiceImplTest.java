package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    UserServiceImpl userServiceImpl;
    @Mock
    UserRepository repository;
    @Mock
    UserMapper userMapper;

    @Test
    void save() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);

        UserDto userDto = new UserDto();
        userDto.setName("user");
        userDto.setEmail("user@user.com");

        Mockito
                .when(userMapper.toUser(Mockito.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDtoOdj = invocationOnMock.getArgument(0, UserDto.class);
                    User user = new User();
                    user.setName(userDtoOdj.getName());
                    user.setEmail(userDtoOdj.getEmail());

                    return user;
                });

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);
                    user.setId(1L);

                    return user;
                });

        Mockito
                .when(userMapper.toUserDto(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);

                    return new UserDto(user.getId(), user.getName(), user.getEmail());
                });

        UserDto userCreated = userServiceImpl.save(userDto);

        assertThat(userCreated.getId(), notNullValue());
        assertThat(userCreated.getName(), equalTo(userDto.getName()));
        assertThat(userCreated.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void patchUpdateWhenNotFoundException() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);

        Long userNotFound = 9999L;
        UserDto userDto = new UserDto();

        Mockito
                .when(repository.findById(userNotFound))
                .thenThrow(new NotFoundException("пользователя с id{" + userNotFound + "} нет в списке пользователей"));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userServiceImpl.patchUpdate(userNotFound, userDto));

        Assertions.assertEquals("пользователя с id{" + userNotFound + "} нет в списке пользователей", exception.getMessage());
    }

    @Test
    void patchUpdateWhenConflictException() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);

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
        userServiceImpl = new UserServiceImpl(repository, userMapper);

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

        Mockito
                .when(userMapper.toUserDto(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);

                    return new UserDto(user.getId(), user.getName(), user.getEmail());
                });

        UserDto updateUserDto = userServiceImpl.patchUpdate(userId, userDto);

        assertThat(updateUserDto.getId(), equalTo(userId));
        assertThat(updateUserDto.getName(), equalTo(userDto.getName()));
        assertThat(updateUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findById() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);

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

        Mockito
                .when(userMapper.toUserDto(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);

                    return new UserDto(user.getId(), user.getName(), user.getEmail());
                });

        userServiceImpl.findById(userId);

        Mockito.verify(repository, Mockito.times(1))
                .findById(userId);

        Mockito.verify(userMapper, Mockito.times(1))
                .toUserDto(Mockito.any(User.class));
    }

    @Test
    void findAll() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);

        Integer numberPage = 0;
        Integer numberUserToView = 10;

        Mockito
                .when(repository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());

        Mockito
                .when(userMapper.toUserDto(Mockito.anyList()))
                .thenReturn(Collections.emptyList());

        userServiceImpl.findAll(numberPage, numberUserToView);

        Mockito.verify(repository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));

        Mockito.verify(userMapper, Mockito.times(1))
                .toUserDto(Mockito.anyList());
    }

    @Test
    void deleteById() {
        userServiceImpl = new UserServiceImpl(repository, userMapper);
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

}