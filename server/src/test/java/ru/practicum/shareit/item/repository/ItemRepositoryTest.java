package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsIterableContaining.hasItem;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

    @Test
    void findSearchLineInNameAndDescription() {
        User ownerItem = makeUser("owner", "owner@ya.ru");
        em.persist(ownerItem);
        em.flush();

        List<Item> sourceItems = List.of(
                makeItem("дрель", "обычная дрель", true, ownerItem, null),
                makeItem("дрель обычная", "дрель", true, ownerItem, null));

        em.persist(sourceItems.get(0));
        em.persist(sourceItems.get(1));
        em.flush();

        List<Item> items = repository.findSearchLineInNameAndDescription("обычная", true,
                PageRequest.of(0, 10)).toList();

        assertThat(items, hasSize(sourceItems.size()));

        for (Item sourceItem : sourceItems) {
            assertThat(items, hasItem(allOf(
                    hasProperty("name", equalTo(sourceItem.getName()))
            )));
        }
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
}