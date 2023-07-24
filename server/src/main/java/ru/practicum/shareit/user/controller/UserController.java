package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUpdate(@PathVariable Long id,
                               @RequestBody UserDto userDto,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userService.patchUpdate(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id,
                            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> findAll(@RequestParam(value = "numberPage", defaultValue = "0") Integer numberPage,
                                 @RequestParam(value = "numberUserToView", defaultValue = "30") Integer numberUserToView,
                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return userService.findAll(numberPage, numberUserToView);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        userService.deleteById(id);
    }
}