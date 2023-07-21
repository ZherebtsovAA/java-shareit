package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwner(User owner, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM items " +
            "WHERE available = :available AND UPPER(TRIM(name || description)) LIKE '%' || UPPER(TRIM(:searchLine)) || '%'",
            nativeQuery = true)
    Page<Item> findSearchLineInNameAndDescription(@Param("searchLine") String searchLine,
                                                  @Param("available") Boolean available,
                                                  Pageable pageable);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.request IN (:itemRequests)")
    List<Item> findAllItemByRequestId(@Param("itemRequests") Collection<ItemRequest> itemRequests);
}