package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemAndStatusIn(Item item, Collection<BookingStatus> status, Sort sort);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker = ?1 AND b.status IN ('APPROVED', 'REJECTED') " +
            "AND b.start <= LOCALTIMESTAMP AND b.end > LOCALTIMESTAMP")
    Page<Booking> findByBookerWhereStatusCurrent(User booker, Pageable pageable);

    Page<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findByBooker(User booker, Pageable pageable);

    Page<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStatusInAndStartAfter(User booker, Collection<BookingStatus> status,
                                                       LocalDateTime start, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item IN (SELECT i FROM Item i WHERE i.owner = ?1)")
    Page<Booking> findBookingForAllItemByUser(User owner, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status = ?2 AND b.item IN (SELECT i FROM Item i WHERE i.owner = ?1)")
    Page<Booking> findBookingForAllItemByUser(User owner, BookingStatus status, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status IN ('APPROVED', 'REJECTED') " +
            "AND b.start <= LOCALTIMESTAMP AND b.end > LOCALTIMESTAMP " +
            "AND b.item IN (SELECT i FROM Item i WHERE i.owner = ?1)")
    Page<Booking> findBookingAllItemByUserWhereStatusCurrent(User owner, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.end < ?2 AND b.item IN (SELECT i FROM Item i WHERE i.owner = ?1)")
    Page<Booking> findBookingForAllItemByUserWhereEndBefore(User owner, LocalDateTime end, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status IN (?2) AND b.start > ?3 AND b.item IN (SELECT i FROM Item i WHERE i.owner = ?1)")
    Page<Booking> findBookingForAllItemByUserWhereStartAfter(User owner, Collection<BookingStatus> status,
                                                             LocalDateTime start, Pageable pageable);

    @Query(value =
            "SELECT * " +
                    "FROM bookings " +
                    "WHERE item_id = ?1 AND status ='APPROVED' AND start_date < ?2 ORDER BY end_date DESC LIMIT 1",
            nativeQuery = true)
    Booking findLastBookingByItem(Long itemId, LocalDateTime currentDateTime);

    @Query(value =
            "SELECT * " +
                    "FROM bookings " +
                    "WHERE item_id = ?1 AND status ='APPROVED' AND start_date > ?2 ORDER BY start_date ASC LIMIT 1",
            nativeQuery = true)
    Booking findNextBookingByItem(Long itemId, LocalDateTime currentDateTime);

    Optional<Booking> findFirst1ByItemAndBookerAndEndBeforeOrderByEndDesc(Item item, User booker,
                                                                          LocalDateTime currentDateTime);

}