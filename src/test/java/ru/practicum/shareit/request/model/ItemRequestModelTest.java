package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestModelTest {

    @Test
    void testEquals() {
        ItemRequest first = makeItemRequest(1L, "descriptionFirst", null, null);
        ItemRequest second = makeItemRequest(2L, "descriptionSecond", null, null);

        assertTrue(first.equals(first));
        assertFalse(first.equals(second));
    }


    private ItemRequest makeItemRequest(Long id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        return itemRequest;
    }

}