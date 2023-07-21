package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository repository;

    @Test
    void findByItemOrderByCreatedDesc() {
        User user = makeUser("user", "user@ya.ru");
        em.persist(user);
        em.flush();

        Item sourceItem = makeItem("дрель", "обычная дрель", true, user, null);
        em.persist(sourceItem);
        em.flush();

        List<Comment> sourceComments = List.of(
                makeComment("comment №1", sourceItem, user, LocalDateTime.now()),
                makeComment("comment №2", sourceItem, user, LocalDateTime.now().minusDays(2)),
                makeComment("comment №3", sourceItem, user, LocalDateTime.now().minusDays(1))
        );
        em.persist(sourceComments.get(0));
        em.persist(sourceComments.get(1));
        em.persist(sourceComments.get(2));
        em.flush();

        List<Comment> comments = repository.findByItemOrderByCreatedDesc(sourceItem);

        assertThat(comments, hasSize(sourceComments.size()));
        assertThat(comments.get(0).getText(), is(equalTo(sourceComments.get(0).getText())));
        assertThat(comments.get(1).getText(), is(equalTo(sourceComments.get(2).getText())));
        assertThat(comments.get(2).getText(), is(equalTo(sourceComments.get(1).getText())));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item makeItem(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }

    private Comment makeComment(String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }
}