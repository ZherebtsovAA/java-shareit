package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        User user = repository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto patchUpdate(Long userId, UserDto userDto) throws NotFoundException, ConflictException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        String userDtoEmail = userDto.getEmail();
        if (userDtoEmail != null) {
            Optional<User> foundUser = repository.findByEmailContainingIgnoreCase(userDtoEmail);
            if (foundUser.isPresent()) {
                if (!Objects.equals(foundUser.get().getId(), userId)) {
                    throw new ConflictException("email{" + userDtoEmail + "} уже используется");
                }
            }
            user.setEmail(userDtoEmail);
        }

        return userMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto findById(Long userId) throws NotFoundException {
        User result = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        return userMapper.toUserDto(result);
    }

    @Override
    public List<UserDto> findAll(Integer numberPage, Integer numberUserToView) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(numberPage, numberUserToView, sortById);
        List<User> users = repository.findAll(page).getContent();
        return userMapper.toUserDto(users);
    }

    @Transactional
    @Override
    public void deleteById(Long userId) throws NotFoundException {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        repository.deleteById(userId);
    }

}