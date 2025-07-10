package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetUserAndOwnerBookingTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private static User owner1, owner2, booker, bookerWithoutBookings;
    private static Booking waitingBooking, futureBooking, pastBooking,
            rejectedBooking, canceledBooking, currentBooking;

    @BeforeEach
    public void setUp() {
        owner1 = User.builder().name("user1").email("user1@mail.com").password("12345678").build();
        owner2 = User.builder().name("user2").email("user2@mail.com").password("12345678").build();
        booker = User.builder().name("booker1").email("booker1@mail.com").password("12345678").build();
        bookerWithoutBookings = User.builder().name("booker2").email("booker2@mail.com").password("12345678").build();
        userService.createUser(owner1);
        userService.createUser(owner2);
        userService.createUser(booker);
        userService.createUser(bookerWithoutBookings);

        Item itemByOwner1 = Item.builder()
                .name("item1")
                .description("description for i1")
                .available(true)
                .owner(owner1)
                .build();
        Item itemByOwner2 = Item.builder()
                .name("item2")
                .description("description for i2")
                .available(true)
                .owner(owner2)
                .build();
        itemService.createItem(itemByOwner1);
        itemService.createItem(itemByOwner2);

        waitingBooking = new Booking(null, LocalDateTime.now().plusDays(14), LocalDateTime.now().plusDays(28),
                itemByOwner1, booker, Status.WAITING);
        futureBooking = new Booking(null, LocalDateTime.now().plusDays(14), LocalDateTime.now().plusDays(28),
                itemByOwner2, booker, Status.APPROVED);
        pastBooking = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemByOwner1, booker, Status.APPROVED);
        rejectedBooking = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemByOwner2, booker, Status.REJECTED);
        canceledBooking = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemByOwner1, booker, Status.CANCELED);
        currentBooking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(24),
                itemByOwner1, booker, Status.APPROVED);

        bookingService.saveBooking(waitingBooking);
        bookingService.saveBooking(futureBooking);
        bookingService.saveBooking(pastBooking);
        bookingService.saveBooking(rejectedBooking);
        bookingService.saveBooking(canceledBooking);
        bookingService.saveBooking(currentBooking);
    }

    @Test
    public void shouldGetUserBookings() {
        List<Booking> bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.WAITING);
        List<Booking> bookingsForBookerWithoutBookings = bookingService.getUserBookings(bookerWithoutBookings.getId(), State.WAITING);
        assertThat(bookingsForBooker, hasSize(1));
        assertThat(bookingsForBooker, hasItems(waitingBooking));
        assertThat(bookingsForBookerWithoutBookings, is(empty()));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.PAST);
        assertThat(bookingsForBooker, hasSize(1));
        assertThat(bookingsForBooker, hasItem(pastBooking));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.REJECTED);
        assertThat(bookingsForBooker, hasSize(2));
        assertThat(bookingsForBooker, hasItems(rejectedBooking, canceledBooking));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.FUTURE);
        assertThat(bookingsForBooker, hasSize(2));
        assertThat(bookingsForBooker, hasItems(futureBooking, waitingBooking));
        bookingService.approveBooking(waitingBooking.getId(), false, owner1.getId());
        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.FUTURE);
        assertThat(bookingsForBooker, hasSize(1));
        assertThat(bookingsForBooker, hasItem(futureBooking));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.REJECTED);
        assertThat(bookingsForBooker, hasSize(3));
        assertThat(bookingsForBooker, hasItems(rejectedBooking, canceledBooking, waitingBooking));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.CURRENT);
        assertThat(bookingsForBooker, hasSize(1));
        assertThat(bookingsForBooker, hasItem(currentBooking));

        bookingsForBooker = bookingService.getUserBookings(booker.getId(), State.ALL);
        assertThat(bookingsForBooker, hasSize(6));
    }

    @Test
    public void shouldGetOwnerBookings() {
        List<Booking> bookingsForBooker = bookingService.getOwnerBookings(booker.getId(), State.WAITING);
        assertThat(bookingsForBooker, is(empty()));

        List<Booking> bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.WAITING);
        List<Booking> bookingsForOwner2 = bookingService.getOwnerBookings(owner2.getId(), State.WAITING);
        assertThat(bookingsForOwner1, hasSize(1));
        assertThat(bookingsForOwner1, hasItem(waitingBooking));
        assertThat(bookingsForOwner2, is(empty()));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.PAST);
        bookingsForOwner2 = bookingService.getOwnerBookings(owner2.getId(), State.PAST);
        assertThat(bookingsForOwner1, hasSize(1));
        assertThat(bookingsForOwner1, hasItem(pastBooking));
        assertThat(bookingsForOwner2, is(empty()));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.REJECTED);
        bookingsForOwner2 = bookingService.getOwnerBookings(owner2.getId(), State.REJECTED);
        assertThat(bookingsForOwner1, hasSize(1));
        assertThat(bookingsForOwner1, hasItem(canceledBooking));
        assertThat(bookingsForOwner2, hasSize(1));
        assertThat(bookingsForOwner2, hasItem(rejectedBooking));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.FUTURE);
        bookingsForOwner2 = bookingService.getOwnerBookings(owner2.getId(), State.FUTURE);
        assertThat(bookingsForOwner1, hasSize(1));
        assertThat(bookingsForOwner1, hasItem(waitingBooking));
        assertThat(bookingsForOwner2, hasSize(1));
        assertThat(bookingsForOwner2, hasItem(futureBooking));
        bookingService.approveBooking(waitingBooking.getId(), false, owner1.getId());
        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.FUTURE);
        assertThat(bookingsForOwner1, is(empty()));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.REJECTED);
        assertThat(bookingsForOwner1, hasSize(2));
        assertThat(bookingsForOwner1, hasItems(canceledBooking, waitingBooking));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.CURRENT);
        bookingsForOwner2 = bookingService.getOwnerBookings(owner2.getId(), State.CURRENT);
        assertThat(bookingsForOwner1, hasSize(1));
        assertThat(bookingsForOwner1, hasItem(currentBooking));
        assertThat(bookingsForOwner2, is(empty()));

        bookingsForOwner1 = bookingService.getOwnerBookings(owner1.getId(), State.ALL);
        assertThat(bookingsForOwner1, hasSize(4));
    }
}
