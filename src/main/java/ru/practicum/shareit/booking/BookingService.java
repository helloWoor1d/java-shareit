package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    public Booking saveBooking(Booking booking) {
        userService.getUser(booking.getBooker().getId());
        Item item = itemService.getItem(booking.getItem().getId(), booking.getBooker().getId());

        if (!item.getAvailable()) throw new BadOperationException("Вещь недоступна для аренды");
        isEndAfterStart(booking.getStart(), booking.getEnd());
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Владелец не может брать в аренду свою вещь");
        }
        Booking created = repository.save(booking);
        log.debug("Арендована вещь с id {}, пользователем с id {}", created.getId(), created.getBooker());
        return booking;
    }

    public Booking approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с id " + bookingId + " не найдено")
        );
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (!booking.getStatus().equals(Status.WAITING)) throw new BadOperationException("Нельзя изменить статус уже подтвержденного бронирования");
            if (approved.equals(true)) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            repository.save(booking);
        } else {
            throw new BadOperationException("Статус бронирования может изменяться только владельцем вещи!");
        }
        log.debug("Статус бронирования вещи с id {}, изменен на {} ", bookingId, approved);
        return booking;
    }

    public Booking getBooking(Long bookingId, Long userId) {
        userService.getUser(userId);
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с id " + bookingId + " не найдено")
        );
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            log.debug("Получена информация о бронировании с id {} ", bookingId);
            return booking;
        } else {
            throw new NotFoundException("Информацию о бронировании могут просматривать только создатель бронирования или владелец вещи");
        }
    }

    public List<Booking> getUserBookings(Long userId, State state) {
        userService.getUser(userId);
        log.debug("Получен список бронирований со статусом {} пользователя с id {} ", state, userId);
        switch (state) {
            case ALL:
                return repository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return repository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return repository.findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(userId, Status.APPROVED, LocalDateTime.now());
            case FUTURE:
                return repository.findAllByBookerIdAndStatusOrStatusAndStartAfterOrderByStartDesc(userId, Status.APPROVED, Status.WAITING, LocalDateTime.now());
            case WAITING:
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return repository.findAllByBookerIdAndStatusOrStatusOrderByStartDesc(userId, Status.REJECTED, Status.CANCELED);
            default:
                return new ArrayList<>();
        }
    }

    public List<Booking> getOwnerBookings(Long ownerId, State state) {
        userService.getUser(ownerId);
        List<Long> userItemsId =  itemService.getUserItems(ownerId).stream()
                .map(Item::getId).collect(Collectors.toList());
        log.debug("Получен список бронирования вещей со статусом {}. Id владельца {}", state, ownerId);
        switch (state) {
            case ALL:
                return repository.findAllByItemIdInOrderByStartDesc(userItemsId);
            case CURRENT:
                return repository.findAllByItemIdInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userItemsId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return repository.findAllByItemIdInAndStatusAndEndBeforeOrderByStartDesc(userItemsId, Status.APPROVED, LocalDateTime.now());
            case FUTURE:
                return repository.findAllByItemIdInAndStatusOrStatusAndStartAfterOrderByStartDesc(userItemsId, Status.APPROVED, Status.WAITING, LocalDateTime.now());
            case WAITING:
                return repository.findAllByItemIdInAndStatusOrderByStartDesc(userItemsId, Status.WAITING);
            case REJECTED:
                return repository.findAllByItemIdInAndStatusOrStatusOrderByStartDesc(userItemsId, Status.REJECTED, Status.CANCELED);
            default:
                return new ArrayList<>();
        }
    }

    public Map<Long, BookingShort> getLastBooking(List<Long> itemsId) {
        List<BookingShort> bookings = repository.getLastBookings(itemsId, Status.APPROVED, LocalDateTime.now());
        return bookings.stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));
    }

    public Map<Long, BookingShort> getNextBooking(List<Long> itemsId) {
        List<BookingShort> bookings = repository.findFirstByItemIdInAndStatusAndStartAfterOrderByStart(itemsId, Status.APPROVED, LocalDateTime.now());
        return bookings.stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));
    }

    private void isEndAfterStart(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new ValidationException("Дата начала бронирования должна быть до даты окончания");
        }
    }
}
