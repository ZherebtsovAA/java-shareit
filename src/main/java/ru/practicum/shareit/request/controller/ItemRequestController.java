package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto,
                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemRequestDto);

        return itemRequestService.save(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(@PathVariable @PositiveOrZero Long requestId,
                                           @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestService.findById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findYourRequests(@RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
                                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestService.findYourRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findOtherRequests(
            @RequestHeader(X_SHARER_USER_ID) @PositiveOrZero Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return itemRequestService.findOtherRequests(userId, from, size);
    }

}