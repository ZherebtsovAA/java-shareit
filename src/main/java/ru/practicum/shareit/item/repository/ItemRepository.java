package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwner(User owner, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM items " +
            "WHERE available = ?2 AND UPPER(TRIM(name || description)) LIKE '%' || UPPER(TRIM(?1)) || '%'",
            nativeQuery = true)
    Page<Item> findSearchLineInNameAndDescription(String searchLine, Boolean available, Pageable pageable);

    List<Item> findByRequest_Id(Long requestId);

}