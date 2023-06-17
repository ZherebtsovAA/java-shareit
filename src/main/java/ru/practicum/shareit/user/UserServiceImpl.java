package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto save(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUpdate(Long userId, UserDto userDto) {
        findById(userId);
        User user = repository.patchUpdate(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) throws NotFoundException {
        Optional<User> result = repository.findById(userId);
        if (result.isEmpty()) {
            throw new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей");
        }
        return UserMapper.toUserDto(result.get());
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return UserMapper.toUserDto(users);
    }

    @Override
    public void deleteById(Long userId) {
        Optional<User> result = repository.findById(userId);
        if (result.isEmpty()) {
            throw new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей");
        }
        repository.deleteById(userId);
    }
}