package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithLastAndNextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    public ItemDto toItemDto(Item item) {
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                ownerId, requestId);
    }

    public abstract List<ItemDto> toItemDto(Iterable<Item> items);

    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "user", target = "owner")
    public abstract Item toItem(ItemDto itemDto, User user);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.owner.id", target = "ownerId")
    @Mapping(source = "item.request.id", target = "requestId")
    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    public abstract ItemDtoWithLastAndNextBooking toItemDtoWithLastAndNextBooking(
            Item item, BookingResponseDto lastBooking, BookingResponseDto nextBooking, List<CommentDto> comments);

}