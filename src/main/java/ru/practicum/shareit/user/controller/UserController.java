package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.group.UserMarker;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated({UserMarker.OnCreate.class})
    public UserDto create(@Valid @RequestBody UserDto userDto,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUpdate(@PathVariable @PositiveOrZero Long id,
                               @Valid @RequestBody UserDto userDto,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userService.patchUpdate(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable @PositiveOrZero Long id,
                            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> findAll(@RequestParam(value = "numberPage", defaultValue = "0") @PositiveOrZero Integer numberPage,
                                 @RequestParam(value = "numberUserToView", defaultValue = "30") @Positive Integer numberUserToView,
                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return userService.findAll(numberPage, numberUserToView);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @PositiveOrZero Long id,
                       HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        userService.deleteById(id);
    }
}