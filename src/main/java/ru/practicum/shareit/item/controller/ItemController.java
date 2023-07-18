package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                          @Valid @RequestBody ItemDto itemDto,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemDto);

        return itemService.save(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable @PositiveOrZero Long itemId,
                                    @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, commentDto);

        if (commentDto.getCreated() == null) {
            commentDto.setCreated(LocalDateTime.now());
        }

        return itemService.saveComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchUpdate(@PathVariable @PositiveOrZero Long id,
                               @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                               @RequestBody ItemDto itemDto,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemDto);

        return itemService.patchUpdate(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDtoWithLastAndNextBooking findById(@PathVariable @PositiveOrZero Long id,
                                                  @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                                  HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemService.findById(id, userId);
    }

    @GetMapping
    public List<ItemDtoWithLastAndNextBooking> findAllOwnerItem(
            @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return itemService.findAllOwnerItem(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameAndDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "available", defaultValue = "true") Boolean available,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return itemService.findByNameAndDescription(text, available, from, size);
    }

}