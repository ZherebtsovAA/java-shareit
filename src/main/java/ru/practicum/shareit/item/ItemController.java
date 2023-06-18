package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @PatchMapping("/{id}")
    public ItemDto patchUpdate(@PathVariable("id") @Min(0) Long itemId,
                               @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody ItemDto itemDto) {
        return itemService.patchUpdate(itemId, userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable("id") @Min(0) Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAllOwnerItem(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllOwnerItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameAndDescription(@RequestParam(value = "text", required = true) String text,
                                                  @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit,
                                                  @RequestParam(value = "available", defaultValue = "true", required = false) Boolean available) {
        return itemService.findByNameAndDescription(text, limit, available);
    }

}