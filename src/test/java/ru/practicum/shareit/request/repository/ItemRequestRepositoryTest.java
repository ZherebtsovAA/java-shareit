package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

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
    }

    @Test
    void findByRequestorNotOrderByCreatedDesc() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);

        User user = em.persist(makeUser(null, "user", "user@user.com"));
        User otherUser = em.persist(makeUser(null, "otherUser", "otherUser@user.com"));
        em.flush();

        ItemRequest itemRequest = makeItemRequest(null, "Хотел бы воспользоваться щёткой для обуви", otherUser, LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();

        Page<ItemRequest> found = repository.findByRequestorNotOrderByCreatedDesc(user, pageable);

        assertThat(found.getContent(), hasSize(1));
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