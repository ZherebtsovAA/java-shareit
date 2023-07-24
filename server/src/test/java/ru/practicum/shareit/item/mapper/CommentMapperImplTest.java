package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CommentMapperImplTest {
    private CommentMapperImpl commentMapper;

    @BeforeEach
    void beforeEach() {
        commentMapper = new CommentMapperImpl();
    }

    @Test
    void toComment() {
        CommentDto commentDto = null;

        assertThat(commentMapper.toComment(commentDto, null, null), equalTo(null));
    }

    @Test
    void toCommentDto() {
        Comment comment = null;

        assertThat(commentMapper.toCommentDto(comment), equalTo(null));
    }

    @Test
    void testToCommentDto() {
        List<Comment> comments = null;

        assertThat(commentMapper.toCommentDto(comments), equalTo(null));
    }
}