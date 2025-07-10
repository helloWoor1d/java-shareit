package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i from Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))" +
            "AND i.available")
    List<Item> search(String text);

    <T> Optional<T> findById(long id, Class<T> type);

    List<ItemShort> findAllByIdIn(List<Long> ids);

    @Query("SELECT b FROM Item i " +
            "JOIN Booking b ON i.id = b.item.id " +
            "WHERE b.booker.id = ?1 " +
            " and b.item.id = ?2" +
            " and b.end < ?3")
    List<Booking> getUserItemBookings(Long userId, Long itemId, LocalDateTime end);

    List<Item> findAllByRequestIdIn(List<Long> requestId);
}
