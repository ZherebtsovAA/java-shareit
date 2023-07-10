package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto) throws NotFoundException;

    CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) throws NotFoundException, BadRequestException;

    ItemDto patchUpdate(Long itemId, Long userId, ItemDto itemDto) throws NotFoundException;

    ItemDtoWithLastAndNextBooking findById(Long itemId, Long userId) throws NotFoundException;

    List<ItemDtoWithLastAndNextBooking> findAllOwnerItem(Long ownerId, Integer from, Integer size) throws BadRequestException;

    List<ItemDto> findByNameAndDescription(String searchLine, Boolean available, Integer from, Integer size) throws BadRequestException;
}