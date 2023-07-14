package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository repository;

    @Test
    void findByItemOrderByCreatedDesc() {
    }

}