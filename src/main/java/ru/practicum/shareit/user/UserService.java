package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto patchUpdate(Long userId, UserDto userDto);

    UserDto findById(Long userId) throws NotFoundException;

    List<UserDto> findAll();

    void deleteById(Long userId);
}