package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommentModelTest {

    @Test
    void testEquals() {
        Comment first = makeComment(1L, "firstComment", null, null, null);
        Comment second = makeComment(2L, "secondComment", null, null, null);

        assertFalse(first.equals(second));
        assertNotEquals(first.hashCode(), second.hashCode());
    }

    private Comment makeComment(Long id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }

}