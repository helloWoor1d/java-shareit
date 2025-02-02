package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingMapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserShort;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingMapping bookingMapping;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BookingGetDto> createBooking(@Valid @RequestBody BookingCreateDto bookingDto,
                                                       @RequestHeader(USER_ID_HEADER) long userId) {
        Booking booking = bookingMapping.fromDto(bookingDto, userId);
        return ResponseEntity.ok(bookingMapping.toDto(
                bookingService.saveBooking(booking)));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingGetDto> approveBooking(@PathVariable long bookingId,
                                                        @RequestParam boolean approved,
                                                        @RequestHeader(USER_ID_HEADER) long userId) {
        return ResponseEntity.ok(bookingMapping.toDto(
                bookingService.approveBooking(bookingId, approved, userId)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingGetDto> getBooking(@PathVariable long bookingId,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return ResponseEntity.ok(bookingMapping.toDto(bookingService.getBooking(bookingId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<BookingGetDto>> getUserBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) {
        try {
            List<Booking> userBookings = bookingService.getUserBookings(userId, State.valueOf(state.toUpperCase()));
            return ResponseEntity.ok(listBookingsToDto(userBookings));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingGetDto>> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        try {
            List<Booking> ownerBookings = bookingService.getOwnerBookings(userId, State.valueOf(state.toUpperCase()));
            return ResponseEntity.ok(listBookingsToDto(ownerBookings));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private List<BookingGetDto> listBookingsToDto(List<Booking> bookings) {
        List<Long> usersId = bookings.stream().map(b -> b.getBooker().getId()).collect(Collectors.toList());
        List<Long> itemsId = bookings.stream().map(b -> b.getItem().getId()).collect(Collectors.toList());

        List<UserShort> users = userService.getShortUsersByIds(usersId);
        List<ItemShort> items = itemService.getShortItemsByIds(itemsId);

        List<BookingGetDto> bookingsDto = bookings.stream()
                .map(booking -> {
                    UserShort booker = users.stream()
                            .filter(u -> u.getId().equals(booking.getBooker().getId()))
                            .findFirst().orElse(null);
                    ItemShort item = items.stream()
                            .filter(i -> i.getId().equals(booking.getItem().getId()))
                            .findFirst().orElse(null);
                    return bookingMapping.toDto(booking, booker, item);
                }).collect(Collectors.toList());
        return bookingsDto;
    }
}
