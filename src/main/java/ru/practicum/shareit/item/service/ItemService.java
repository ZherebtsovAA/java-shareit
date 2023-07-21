package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto);

    ItemDto patchUpdate(Long itemId, Long userId, ItemDto itemDto);

    ItemDtoWithLastAndNextBooking findById(Long itemId, Long userId);

    List<ItemDtoWithLastAndNextBooking> findAllOwnerItem(Long ownerId, Integer from, Integer size);

    List<ItemDto> findByNameAndDescription(String searchLine, Boolean available, Integer from, Integer size);
}