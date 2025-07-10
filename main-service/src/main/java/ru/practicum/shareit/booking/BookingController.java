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
import ru.practicum.shareit.booking.dto.BookingMappingImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMappingImpl bookingMapping;

    @PostMapping
    public ResponseEntity<BookingGetDto> createBooking(@Valid @RequestBody BookingCreateDto bookingDto,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        Booking booking = bookingMapping.fromDto(bookingDto, userId);
        return ResponseEntity.ok(bookingMapping.toDto(
                bookingService.saveBooking(booking)));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingGetDto> approveBooking(@PathVariable Long bookingId,
                                                        @RequestParam Boolean approved,
                                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(bookingMapping.toDto(
                bookingService.approveBooking(bookingId, approved, userId)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingGetDto> getBooking(@PathVariable Long bookingId,
                                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(bookingMapping.toDto(bookingService.getBooking(bookingId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<BookingGetDto>> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) {
        try {
            List<Booking> userBookings = bookingService.getUserBookings(userId, State.valueOf(state.toUpperCase()));
            return ResponseEntity.ok(bookingMapping.listBookingsToDto(userBookings));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingGetDto>> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        try {
            List<Booking> ownerBookings = bookingService.getOwnerBookings(userId, State.valueOf(state.toUpperCase()));
            return ResponseEntity.ok(bookingMapping.listBookingsToDto(ownerBookings));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
