package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserDto userDto) {
        User user = repository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUpdate(Long userId, UserDto userDto) {
        findById(userId);
        User user = repository.patchUpdate(userId, userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) throws NotFoundException {
        User result = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        return userMapper.toUserDto(result);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return userMapper.toUserDto(users);
    }

    @Override
    public void deleteById(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        repository.deleteById(userId);
    }

}