package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemMapperImplTest {
    private ItemMapperImpl itemMapper;

    @BeforeEach
    void beforeEach() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void toItemDto() {
        List<Item> items = null;

        assertThat(itemMapper.toItemDto(items), equalTo(null));
    }

    @Test
    void toItem() {
        ItemDto itemDto = null;

        assertThat(itemMapper.toItem(itemDto, null, null), equalTo(null));
    }

    @Test
    void toItemDtoWithLastAndNextBooking() {
        Item item = null;

        assertThat(itemMapper.toItemDtoWithLastAndNextBooking(item, null, null, null),
                equalTo(null));
    }

}