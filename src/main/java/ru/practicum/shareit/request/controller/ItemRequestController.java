package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.save(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(@PathVariable("requestId") @Min(0) Long requestId,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemRequestService.findById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findYourRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemRequestService.findYourRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findOtherRequests(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return itemRequestService.findOtherRequests(userId, from, size);
    }

}