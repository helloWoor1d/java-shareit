package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
												@Valid @RequestBody BookingCreateDto requestDto) {
		log.info("Получен запрос на создание бронирования {} пользователем с id {}", requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
												 @RequestParam boolean approved,
												 @PathVariable long bookingId) {
		log.info("Получен запрос от пользователя {} на изменение статуса бронирования {}  на {}", userId, bookingId, approved);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
											 @PathVariable long bookingId) {
		log.info("Получен запрос от пользователя {} на получение бронирования {}", userId, bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER) long userId,
												  @RequestParam(defaultValue = "ALL") String stateParam) {
		log.info("Получен запрос от пользователя {} на получение списка бронирований со статусом {}", userId, stateParam);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getUserBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
												   @RequestParam(defaultValue = "ALL") String stateParam) {
		log.info("Получен запрос от владельца {} на получение бронирований вещей со статусом {}", userId, stateParam);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getOwnerBookings(userId, state);
	}
}
