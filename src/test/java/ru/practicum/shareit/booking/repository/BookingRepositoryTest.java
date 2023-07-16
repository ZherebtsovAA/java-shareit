package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

    @Test
    void findByItemAndStatusIn() {
    }

    @Test
    void findByBookerWhereStatusCurrent() {
    }

    @Test
    void findByBookerAndStatus() {
    }

    @Test
    void findByBooker() {
    }

    @Test
    void findByBookerAndEndBefore() {
    }

    @Test
    void findByBookerAndStatusInAndStartAfter() {
    }

    @Test
    void findBookingForAllItemByUser() {
    }

    @Test
    void testFindBookingForAllItemByUser() {
    }

    @Test
    void findBookingAllItemByUserWhereStatusCurrent() {
    }

    @Test
    void findBookingForAllItemByUserWhereEndBefore() {
    }

    @Test
    void findBookingForAllItemByUserWhereStartAfter() {
    }

    @Test
    void findLastBookingByItem() {
    }

    @Test
    void findNextBookingByItem() {
    }

    @Test
    void findFirst1ByItemAndBookerAndEndBeforeOrderByEndDesc() {
    }

}