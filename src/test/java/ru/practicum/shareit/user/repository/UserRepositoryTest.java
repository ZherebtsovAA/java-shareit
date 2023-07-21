package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository repository;

    @Test
    void findByEmail() {
        long userId = 1L;
        User user = makeUser(null, "name", "name@email.ru");
        em.persist(user);
        em.flush();

        User found = repository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        assertThat(found.getEmail())
                .isEqualTo(user.getEmail());
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

}