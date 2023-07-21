package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(source = "itemRequestDto.id", target = "id")
    @Mapping(source = "user", target = "requestor")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user);

    @Mapping(source = "requestor.id", target = "requestorId")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(source = "items", target = "items")
    @Mapping(source = "itemRequest.requestor.id", target = "requestorId")
    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<ItemDto> items);

}