package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") @Min(0) Long itemId,
                                    @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        if (commentDto.getCreated() == null) {
            commentDto.setCreated(LocalDateTime.now());
        }
        return itemService.saveComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchUpdate(@PathVariable("id") @Min(0) Long itemId,
                               @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody ItemDto itemDto) {
        return itemService.patchUpdate(itemId, userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDtoWithLastAndNextBooking findById(@PathVariable("id") @Min(0) Long itemId,
                                                  @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithLastAndNextBooking> findAllOwnerItem(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllOwnerItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameAndDescription(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "available", defaultValue = "true", required = false) Boolean available,
            @RequestParam(value = "numberItemToView", defaultValue = "30", required = false) Integer numberItemToView) {
        return itemService.findByNameAndDescription(text, available, numberItemToView);
    }

}