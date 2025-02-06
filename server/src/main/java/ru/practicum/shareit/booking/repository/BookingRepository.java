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

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status IN (?2, ?3) " +
            "ORDER BY b.start DESC ")
    List<Booking> getUserBookingsRejected(Long userId, Status s1, Status s2);

    List<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(Long id, Status status, LocalDateTime end);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status IN (?2, ?3) " +
            "AND b.start > ?4 " +
            "ORDER BY b.start DESC ")
    List<Booking> getUserBookingsFuture(Long userId, Status s1, Status s2, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsAll(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsWaiting(Long ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status IN (?2, ?3) " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsRejected(Long ownerId, Status s1, Status s2);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start <= ?2 AND b.end >= ?3 " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsCurrent(Long ownerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "AND b.end < ?3 " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsPast(Long ownerId, Status status, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status IN (?2, ?3) " +
            "AND b.start > ?4 " +
            "ORDER BY b.start DESC ")
    List<Booking> getOwnerBookingsFuture(Long ownerId, Status s1, Status s2, LocalDateTime start);

    @Query("SELECT b.id as id, b.booker.id as bookerId, b.item.id as itemId " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.status = ?2 " +
            "AND b.start = (SELECT MIN (b2.start) " +
                            "FROM Booking b2 " +
                            "WHERE b2.item.id = b.item.id " +
                            "AND b2.status = ?2 " +
                            "AND b2.start > ?3) ")
    List<BookingShort> getNextBookings(List<Long> itemId, Status status, LocalDateTime end);

    @Query("SELECT b.id as id, b.booker.id as bookerId, b.item.id as itemId " +
            "FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.status = ?2 " +
            "AND b.start = (SELECT MAX (b2.start) " +
                            "FROM Booking b2 " +
                            "WHERE b2.item.id = b.item.id " +
                            "AND b2.status = ?2 " +
                            "AND (b2.end <= ?3 OR b2.start <= ?3 AND b2.end >= ?3)) ")
    List<BookingShort> getLastBookings(List<Long> itemId, Status status, LocalDateTime now);
}
