package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestResponseDto findById(Long requestId, Long userId);

    List<ItemRequestResponseDto> findYourRequests(Long userId);

    List<ItemRequestResponseDto> findOtherRequests(Long userId, Integer from, Integer size);
}