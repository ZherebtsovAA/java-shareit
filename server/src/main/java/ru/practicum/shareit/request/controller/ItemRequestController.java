package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto,
                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), userId, itemRequestDto);

        return itemRequestService.save(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(@PathVariable Long requestId,
                                           @RequestHeader(X_SHARER_USER_ID) Long userId,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestService.findById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findYourRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), userId);

        return itemRequestService.findYourRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findOtherRequests(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', X_SHARER_USER_ID: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), userId);

        return itemRequestService.findOtherRequests(userId, from, size);
    }
}