package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private static final Sort ID_SORT = Sort.by("id");
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        ItemRequest itemRequest = null;
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("вещь создается на основании не существующего запроса " +
                            "с id{" + requestId + "}"));
        }

        Item item = itemRepository.save(itemMapper.toItem(itemDto, user, itemRequest));

        return itemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещи с id{" + itemId + "} нет в списке вещей"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        bookingRepository.findFirst1ByItemAndBookerAndEndBeforeOrderByEndDesc(item, user, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("извени, но ты не можешь оставить отзыв..."));

        Comment comment = commentRepository.save(commentMapper.toComment(commentDto, item, user));

        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public ItemDto patchUpdate(Long itemId, Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещи с id{" + itemId + "} нет в списке вещей"));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("вещь с id{" + itemId + "} не является вещью пользователя с id{" + userId + "}");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithLastAndNextBooking findById(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещи с id{" + itemId + "} нет в списке вещей"));

        List<Comment> comments = new ArrayList<>(commentRepository.findByItemOrderByCreatedDesc(item));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            return itemMapper.toItemDtoWithLastAndNextBooking(item, null, null, commentMapper.toCommentDto(comments));
        }

        return getLastAndNextBookingForItem(item);
    }

    @Override
    public List<ItemDtoWithLastAndNextBooking> findAllOwnerItem(Long ownerId, Integer from, Integer size) {
        User owner = new User();
        owner.setId(ownerId);

        Pageable pageable = new CustomPageRequest(from, size, ID_SORT.ascending()).getPageRequest();
        Page<Item> itemsOwner = itemRepository.findByOwner(owner, pageable);

        List<ItemDtoWithLastAndNextBooking> finalListBookings = new ArrayList<>();
        for (Item item : itemsOwner) {
            finalListBookings.add(getLastAndNextBookingForItem(item));
        }

        return finalListBookings;
    }

    private ItemDtoWithLastAndNextBooking getLastAndNextBookingForItem(Item item) {
        List<Comment> comments = new ArrayList<>(commentRepository.findByItemOrderByCreatedDesc(item));

        Booking lastBooking = bookingRepository.findLastBookingByItem(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findNextBookingByItem(item.getId(), LocalDateTime.now());
        BookingResponseDto lastBookingDto = bookingMapper.toBookingResponseDto(lastBooking);
        BookingResponseDto nextBookingDto = bookingMapper.toBookingResponseDto(nextBooking);

        return itemMapper.toItemDtoWithLastAndNextBooking(item, lastBookingDto, nextBookingDto, commentMapper.toCommentDto(comments));
    }

    @Override
    public List<ItemDto> findByNameAndDescription(String searchLine, Boolean available, Integer from, Integer size) {
        if (searchLine.isEmpty()) {
            return Collections.emptyList();
        }

        Pageable pageable = new CustomPageRequest(from, size).getPageRequest();
        Page<Item> foundItems = itemRepository.findSearchLineInNameAndDescription(searchLine.trim(), available, pageable);

        return itemMapper.toItemDto(foundItems);
    }
}