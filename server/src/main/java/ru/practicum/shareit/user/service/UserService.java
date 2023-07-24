package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto patchUpdate(Long userId, UserDto userDto);

    UserDto findById(Long userId);

    List<UserDto> findAll(Integer numberPage, Integer numberUserToView);

    void deleteById(Long userId);
}