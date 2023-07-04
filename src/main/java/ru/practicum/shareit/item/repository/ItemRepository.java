package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(User owner, Sort sort);

    @Query(value = "SELECT * " +
            "FROM items " +
            "WHERE available = ?2 AND UPPER(TRIM(name || description)) LIKE '%' || UPPER(TRIM(?1)) || '%' " +
            "LIMIT ?3",
            nativeQuery = true)
    List<Item> findSearchLineInNameAndDescription(String searchLine, Boolean available, Integer numberItemToView);

}