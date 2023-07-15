package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemModelTest {

    @Test
    void testEquals() {
        Item first = makeItem(1L, "firstItem", "firstDescription", true, null, null);
        Item second = makeItem(2L, "secondItem", "secondDescription", true, null, null);

        assertFalse(first.equals(second));
        assertNotEquals(first.hashCode(), second.hashCode());
    }

    private Item makeItem(Long id, String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }

}