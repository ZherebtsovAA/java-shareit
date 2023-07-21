package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "WHERE b.booker = :booker AND b.status IN ('APPROVED', 'REJECTED') " +
            "AND b.start <= LOCALTIMESTAMP AND b.end > LOCALTIMESTAMP")
    Page<Booking> findByBookerWhereStatusCurrent(@Param("booker") User booker, Pageable pageable);

    Page<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findByBooker(User booker, Pageable pageable);

    Page<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStatusInAndStartAfter(User booker, Collection<BookingStatus> status,
                                                       LocalDateTime start, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item IN (SELECT i FROM Item i WHERE i.owner = :owner)")
    Page<Booking> findBookingForAllItemByUser(@Param("owner") User owner, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status = :status AND b.item IN (SELECT i FROM Item i WHERE i.owner = :owner)")
    Page<Booking> findBookingForAllItemByUser(@Param("owner") User owner, @Param("status") BookingStatus status, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status IN ('APPROVED', 'REJECTED') " +
            "AND b.start <= LOCALTIMESTAMP AND b.end > LOCALTIMESTAMP " +
            "AND b.item IN (SELECT i FROM Item i WHERE i.owner = :owner)")
    Page<Booking> findBookingAllItemByUserWhereStatusCurrent(@Param("owner") User owner, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.end < :end AND b.item IN (SELECT i FROM Item i WHERE i.owner = :owner)")
    Page<Booking> findBookingForAllItemByUserWhereEndBefore(@Param("owner") User owner, @Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status IN (:status) AND b.start > :start AND b.item IN (SELECT i FROM Item i WHERE i.owner = :owner)")
    Page<Booking> findBookingForAllItemByUserWhereStartAfter(@Param("owner") User owner, @Param("status") Collection<BookingStatus> status,
                                                             @Param("start") LocalDateTime start, Pageable pageable);

    @Query(value =
            "SELECT * " +
                    "FROM bookings " +
                    "WHERE item_id = :itemId AND status ='APPROVED' AND start_date < :currentDateTime ORDER BY end_date DESC LIMIT 1",
            nativeQuery = true)
    Booking findLastBookingByItem(@Param("itemId") Long itemId, @Param("currentDateTime") LocalDateTime currentDateTime);

    @Query(value =
            "SELECT * " +
                    "FROM bookings " +
                    "WHERE item_id = :itemId AND status ='APPROVED' AND start_date > :currentDateTime ORDER BY start_date ASC LIMIT 1",
            nativeQuery = true)
    Booking findNextBookingByItem(@Param("itemId") Long itemId, @Param("currentDateTime") LocalDateTime currentDateTime);

    Optional<Booking> findFirst1ByItemAndBookerAndEndBeforeOrderByEndDesc(Item item, User booker,
                                                                          LocalDateTime currentDateTime);

}