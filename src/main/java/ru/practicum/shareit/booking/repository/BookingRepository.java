package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, Status status);

    List<Booking> findAllByBookerIdAndStatusOrStatusOrderByStartDesc(Long id, Status s1, Status s2);

    List<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(Long id, Status status, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrStatusAndStartAfterOrderByStartDesc(Long id, Status s1, Status s2, LocalDateTime start);

    List<Booking> findAllByItemIdInOrderByStartDesc(List<Long> id);

    List<Booking> findAllByItemIdInAndStatusOrderByStartDesc(List<Long> id, Status status);

    List<Booking> findAllByItemIdInAndStatusOrStatusOrderByStartDesc(List<Long> id, Status s1, Status s2);

    List<Booking> findAllByItemIdInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(List<Long> id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStatusAndEndBeforeOrderByStartDesc(List<Long> id, Status status, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStatusOrStatusAndStartAfterOrderByStartDesc(List<Long> id, Status s1, Status s2, LocalDateTime start);

    List<BookingShort> findFirstByItemIdInAndStatusAndStartAfterOrderByStart(List<Long> itemId, Status status, LocalDateTime end);

    @Query("SELECT b.id as id, b.booker.id as bookerId, b.item.id as itemId " +
            "FROM Booking b " +
            "WHERE b.status = ?2 " +
            "AND b.item.id IN ?1 " +
            "AND (b.end <= ?3 OR " +
            "b.start <= ?3 and b.end >= ?3 ) " +
            "ORDER BY b.start DESC " +
            "FETCH FIRST 1 ROW ONLY ")
    List<BookingShort> getLastBookings(List<Long> itemId, Status status, LocalDateTime now);
}
