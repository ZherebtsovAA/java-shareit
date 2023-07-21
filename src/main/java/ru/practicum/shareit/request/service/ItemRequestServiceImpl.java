package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = repository.save(itemRequestMapper.toItemRequest(itemRequestDto, user));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestResponseDto findById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запроса с id{" + requestId + "} нет в списке запросов на создание вещей"));

        List<ItemDto> items = itemMapper.toItemDto(itemRepository.findAllItemByRequestId(List.of(itemRequest)));

        return itemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> findYourRequests(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        List<ItemRequest> itemRequests = repository.findByRequestorOrderByCreatedDesc(requestor);
        if (itemRequests.size() == 0) {
            return Collections.emptyList();
        }

        List<Item> itemsToItemRequests = itemRepository.findAllItemByRequestId(itemRequests);

        List<ItemRequestResponseDto> itemRequestsResponseDto = new ArrayList<>(itemRequests.size());
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> items = new ArrayList<>();
            Long itemRequestId = itemRequest.getId();
            for (Item item : itemsToItemRequests) {
                if (Objects.equals(item.getRequest().getId(), itemRequestId)) {
                    items.add(itemMapper.toItemDto(item));
                }
            }
            itemRequestsResponseDto.add(itemRequestMapper.toItemRequestResponseDto(itemRequest, items));
        }

        return itemRequestsResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> findOtherRequests(Long userId, Integer from, Integer size) {
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        Pageable pageable = new CustomPageRequest(from, size).getPageRequest();
        Page<ItemRequest> itemRequests = repository.findByRequestorNotOrderByCreatedDesc(otherUser, pageable);

        List<Item> itemsToItemRequests = itemRepository.findAllItemByRequestId(itemRequests.getContent());

        List<ItemRequestResponseDto> itemRequestsResponseDto = new ArrayList<>(itemRequests.getSize());
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> items = new ArrayList<>();
            Long itemRequestId = itemRequest.getId();
            for (Item item : itemsToItemRequests) {
                if (Objects.equals(item.getRequest().getId(), itemRequestId)) {
                    items.add(itemMapper.toItemDto(item));
                }
            }
            itemRequestsResponseDto.add(itemRequestMapper.toItemRequestResponseDto(itemRequest, items));
        }

        return itemRequestsResponseDto;
    }
}