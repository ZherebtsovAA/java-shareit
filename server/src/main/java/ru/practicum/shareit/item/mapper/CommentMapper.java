package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "commentDto.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "author", target = "author")
    Comment toComment(CommentDto commentDto, Item item, User author);

    List<CommentDto> toCommentDto(List<Comment> comments);

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);
}