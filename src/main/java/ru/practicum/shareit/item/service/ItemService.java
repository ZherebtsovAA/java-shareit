package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto patchUpdate(Long itemId, Long userId, ItemDto itemDto);

    ItemDto findById(Long itemId) throws NotFoundException;

    List<ItemDto> findAllOwnerItem(Long ownerId);

    List<ItemDto> findByNameAndDescription(String searchLine, Integer limit, Boolean available);
}