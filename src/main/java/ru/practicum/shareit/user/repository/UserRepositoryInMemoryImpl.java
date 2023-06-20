package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>(); // Key - userId, Value - User

    private Long globalUserId = 1L;

    private Long getNextId() {
        return globalUserId++;
    }

    @Override
    public User save(User user) throws ConflictException {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("пользователь с email{" + user.getEmail() + "} уже занесен в список пользователей");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User patchUpdate(Long userId, User user) throws ConflictException {
        User userInMap = users.get(userId);

        if (user.getName() != null) {
            userInMap.setName(user.getName());
        }

        if (user.getEmail() != null) {
            Optional<User> foundUser = findByEmail(user.getEmail());
            if (foundUser.isPresent()) {
                if (!Objects.equals(foundUser.get().getId(), userId)) {
                    throw new ConflictException("email{" + user.getEmail() + "} уже используется");
                }
            }
            userInMap.setEmail(user.getEmail());
        }

        return userInMap;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<User> findByEmail(String email) {
        List<User> listOfMatches = users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList());

        if (listOfMatches.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(listOfMatches.get(0));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }
}