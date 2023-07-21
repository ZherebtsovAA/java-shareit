package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository repository;

    @Test
    void findByRequestorOrderByCreatedDesc() {
        User user = em.persist(makeUser(null, "user", "user@user.com"));
        em.flush();

        ItemRequest itemRequest = makeItemRequest(null, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();

        List<ItemRequest> found = repository.findByRequestorOrderByCreatedDesc(user);

        assertThat(found, hasSize(1));
        assertThat(found.get(0), hasProperty("id", is(notNullValue())));
        assertThat(found.get(0), hasProperty("description", is(equalTo(itemRequest.getDescription()))));
        assertThat(found.get(0), hasProperty("requestor", is(equalTo(user))));
        assertThat(found.get(0), hasProperty("created", is(equalTo(itemRequest.getCreated()))));
    }

    @Test
    void findByRequestorNotOrderByCreatedDesc() {
        User user = em.persist(makeUser(null, "user", "user@user.com"));
        User otherUser = em.persist(makeUser(null, "otherUser", "otherUser@user.com"));
        em.flush();

        ItemRequest itemRequest = makeItemRequest(null, "Хотел бы воспользоваться щёткой для обуви", otherUser, LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();

        List<ItemRequest> found = repository.findByRequestorNotOrderByCreatedDesc(user, PageRequest.of(0, 10)).getContent();

        assertThat(found, hasSize(1));
        assertThat(found.get(0), hasProperty("id", is(notNullValue())));
        assertThat(found.get(0), hasProperty("description", is(equalTo(itemRequest.getDescription()))));
        assertThat(found.get(0), hasProperty("requestor", is(equalTo(otherUser))));
        assertThat(found.get(0), hasProperty("created", is(equalTo(itemRequest.getCreated()))));

        found = repository.findByRequestorNotOrderByCreatedDesc(otherUser, PageRequest.of(0, 10)).getContent();

        assertThat(found, hasSize(0));
    }

    private ItemRequest makeItemRequest(Long id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        return itemRequest;
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}