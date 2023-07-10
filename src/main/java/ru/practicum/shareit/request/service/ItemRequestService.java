package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto) throws NotFoundException;

    ItemRequestResponseDto findById(Long requestId, Long userId) throws NotFoundException;

    List<ItemRequestResponseDto> findYourRequests(Long userId) throws NotFoundException;

    List<ItemRequestResponseDto> findOtherRequests(Long userId, Integer from, Integer size)
            throws NotFoundException, BadRequestException;

}