package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemRequestDto);

        return itemRequestClient.save(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable @PositiveOrZero Long requestId,
                                           @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestClient.findById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findYourRequests(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                                   HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestClient.findYourRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findOtherRequests(
            @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return itemRequestClient.findOtherRequests(userId, from, size);
    }
}