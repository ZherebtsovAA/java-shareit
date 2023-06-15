package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}