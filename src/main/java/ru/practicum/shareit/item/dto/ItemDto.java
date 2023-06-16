package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long ownerId;
    private Long requestId;
}