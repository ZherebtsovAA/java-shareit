package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                         @Valid @RequestBody ItemDto itemDto,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemDto);

        return itemClient.save(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable @PositiveOrZero Long itemId,
                                                @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, commentDto);

        if (commentDto.getCreated() == null) {
            commentDto.setCreated(LocalDateTime.now());
        }

        return itemClient.saveComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUpdate(@PathVariable @PositiveOrZero Long id,
                                              @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                              @RequestBody ItemDto itemDto,
                                              HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemDto);

        return itemClient.patchUpdate(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable @PositiveOrZero Long id,
                                           @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemClient.findById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwnerItem(
            @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return itemClient.findAllOwnerItem(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByNameAndDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "available", defaultValue = "true") Boolean available,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return itemClient.findByNameAndDescription(text, available, from, size);
    }
}