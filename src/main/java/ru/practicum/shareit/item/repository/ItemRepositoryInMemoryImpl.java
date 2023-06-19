package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>(); // Key - userId, Value - Item

    private Long globalItemId = 1L;

    private Long getNextId() {
        return globalItemId++;
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patchUpdate(Long itemId, Item item) {
        Item itemInMap = items.get(itemId);
        if (item.getName() != null) {
            itemInMap.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemInMap.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemInMap.setAvailable(item.getAvailable());
        }

        return itemInMap;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        }

        return Optional.empty();
    }

    @Override
    public List<Item> findAllOwnerItem(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

}