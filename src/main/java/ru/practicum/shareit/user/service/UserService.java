package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto patchUpdate(Long userId, UserDto userDto) throws NotFoundException, ConflictException;

    UserDto findById(Long userId) throws NotFoundException;

    List<UserDto> findAll(Integer numberPage, Integer numberUserToView);

    void deleteById(Long userId) throws NotFoundException;
}