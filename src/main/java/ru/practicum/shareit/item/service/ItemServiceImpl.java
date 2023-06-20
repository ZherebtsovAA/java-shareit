package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.findById(userId));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patchUpdate(Long itemId, Long userId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.findById(userId));
        ItemDto foundItemDto = findById(itemId);
        if (!Objects.equals(foundItemDto.getOwnerId(), userId)) {
            throw new NotFoundException("вещь с id{" + itemId + "} не является вещью пользователя с id{" + userId + "}");
        }
        Item item = itemRepository.patchUpdate(itemId, itemMapper.toItem(itemDto, user));

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long itemId) throws NotFoundException {
        Item result = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещи с id{" + itemId + "} нет в списке вещей"));
        return itemMapper.toItemDto(result);
    }

    @Override
    public List<ItemDto> findAllOwnerItem(Long ownerId) {
        List<Item> itemsOwner = itemRepository.findAllOwnerItem(ownerId);

        return itemMapper.toItemDto(itemsOwner);
    }

    @Override
    public List<ItemDto> findByNameAndDescription(String searchLine, Integer limit, Boolean available) {
        if (searchLine.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> Objects.equals(item.getAvailable(), available))
                .filter(item -> {
                    String nameAndDescription = (item.getName() + item.getDescription()).toLowerCase();
                    String text = searchLine.toLowerCase();
                    return nameAndDescription.contains(text);
                })
                .limit(limit)
                .collect(Collectors.toList());

        return itemMapper.toItemDto(items);
    }

}