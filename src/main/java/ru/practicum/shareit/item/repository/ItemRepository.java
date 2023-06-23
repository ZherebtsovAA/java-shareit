package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item patchUpdate(Long itemId, Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findAllOwnerItem(Long ownerId);

    List<Item> findAll();
}