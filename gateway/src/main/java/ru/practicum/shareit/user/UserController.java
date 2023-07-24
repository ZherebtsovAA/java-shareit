package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.group.UserMarker;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({UserMarker.OnCreate.class})
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userClient.save(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUpdate(@PathVariable @PositiveOrZero Long id,
                                              @Valid @RequestBody UserDto userDto,
                                              HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userDto);

        return userClient.patchUpdate(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable @PositiveOrZero Long id,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return userClient.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "numberPage", defaultValue = "0") @PositiveOrZero Integer numberPage,
                                          @RequestParam(value = "numberUserToView", defaultValue = "30") @Positive Integer numberUserToView,
                                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return userClient.findAll(numberPage, numberUserToView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable @PositiveOrZero Long id,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return userClient.deleteById(id);
    }
}