package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private static User u1, u2;
    private static Item i1;

    @BeforeEach
    public void setUp() {
        u1 = User.builder().name("User 1").email("user1@gmail.com").password("12345678").build();
        u2 = User.builder().name("User 2").email("user2@gmail.com").password("12345678").build();
        i1 = Item.builder()
                .name("Item 1")
                .description("description for item 1")
                .available(true)
                .owner(u1)
                .build();
        userService.createUser(u1);
        userService.createUser(u2);
        itemService.createItem(i1);
    }

    @Test
    public void shouldCreateBooking() {
        i1.setAvailable(false);
        Booking booking = Booking.builder()
                .item(i1)
                .booker(u1)
                .start(LocalDateTime.now().plusDays(14))
                .end(LocalDateTime.now()).build();

        Exception ex = assertThrows(BadOperationException.class,
                () -> bookingService.saveBooking(booking));
        assertThat(ex.getMessage(), is("Вещь недоступна для аренды"));

        i1.setAvailable(true);
        ex = assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(booking));
        assertThat(ex.getMessage(), is("Дата начала бронирования должна быть до даты окончания"));

        booking.setStart(LocalDateTime.now().minusDays(14));
        booking.setEnd(LocalDateTime.now().plusDays(14));
        ex = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(booking));
        assertThat(ex.getMessage(),is("Владелец не может брать в аренду свою вещь"));

        booking.setBooker(u2);
        Booking savedBooking = bookingService.saveBooking(booking);
        assertThat(savedBooking, hasProperty("id"));
        assertThat(savedBooking, samePropertyValuesAs(booking));
    }

    @Test
    public void approveBookingTest() {
        Exception ex = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(456L, true, 23L));
        assertThat(ex.getMessage(), is("Бронирование с id 456 не найдено"));

        Booking booking = Booking.builder()
                .item(i1)
                .booker(u2)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().plusDays(14))
                .status(Status.APPROVED).build();
        bookingService.saveBooking(booking);

        ex = assertThrows(BadOperationException.class,
                () -> bookingService.approveBooking(booking.getId(), false, u2.getId()));
        assertThat(ex.getMessage(), is("Статус бронирования может изменяться только владельцем вещи!"));

        ex = assertThrows(BadOperationException.class,
                () -> bookingService.approveBooking(booking.getId(), false, u1.getId()));
        assertThat(ex.getMessage(), is("Нельзя изменить статус уже подтвержденного бронирования"));

        booking.setStatus(Status.WAITING);
        Booking approvedBooking = bookingService.approveBooking(booking.getId(), true, u1.getId());
        assertThat(approvedBooking.getStatus(), is(Status.APPROVED));
        assertThat(approvedBooking, samePropertyValuesAs(booking));
    }

    @Test
    public void shouldGetBooking() {
        Exception ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(143L, 143L));
        assertThat(ex.getMessage(), is("Пользователь с id 143 не был найден"));

        ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(456L, u2.getId()));
        assertThat(ex.getMessage(), is("Бронирование с id 456 не найдено"));

        Booking booking = Booking.builder()
                .item(i1)
                .booker(u2)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().plusDays(14))
                .status(Status.APPROVED).build();
        bookingService.saveBooking(booking);
        User user = User.builder().name("New User").email("newUser@mail.ru").password("12345678").build();
        userService.createUser(user);

        ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), user.getId()));
        assertThat(ex.getMessage(), is("Информацию о бронировании могут просматривать только создатель бронирования или владелец вещи"));

        Booking savedBooking = bookingService.getBooking(booking.getId(), u2.getId());
        assertThat(savedBooking, is(booking));
    }
}
