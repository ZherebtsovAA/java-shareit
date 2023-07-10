package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = repository.save(itemRequestMapper.toItemRequest(itemRequestDto, user));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestResponseDto findById(Long requestId, Long userId) throws NotFoundException {
        userService.findById(userId);
        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запроса с id{" + requestId + "} нет в списке запросов на создание вещей"));

        List<ItemDto> items = itemMapper.toItemDto(itemRepository.findByRequest_Id(requestId));

        return itemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> findYourRequests(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));
        List<ItemRequest> itemRequests = repository.findByRequestorOrderByCreatedDesc(user);

        List<ItemRequestResponseDto> itemRequestsResponseDto = new ArrayList<>(itemRequests.size());
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> items = itemMapper.toItemDto(itemRepository.findByRequest_Id(itemRequest.getId()));
            itemRequestsResponseDto.add(itemRequestMapper.toItemRequestResponseDto(itemRequest, items));
        }

        return itemRequestsResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> findOtherRequests(Long userId, Integer from, Integer size)
            throws NotFoundException, BadRequestException {
        if (from < 0) {
            throw new BadRequestException("request param from{" + from + "} не может быть отрицательным");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        Page<ItemRequest> itemRequests = repository.findByRequestorNotOrderByCreatedDesc(user, pageable);

        List<ItemRequestResponseDto> itemRequestsResponseDto = new ArrayList<>(itemRequests.getSize());
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> items = itemMapper.toItemDto(itemRepository.findByRequest_Id(itemRequest.getId()));
            itemRequestsResponseDto.add(itemRequestMapper.toItemRequestResponseDto(itemRequest, items));
        }

        return itemRequestsResponseDto;
    }

}