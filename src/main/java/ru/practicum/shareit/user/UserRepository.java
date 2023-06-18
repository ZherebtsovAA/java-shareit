package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user) throws ConflictException;

    User patchUpdate(Long userId, User user) throws ConflictException;

    Optional<User> findById(Long userId);

    List<User> findAll();

    void deleteById(Long userId);
}