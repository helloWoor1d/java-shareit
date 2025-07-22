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
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserRole;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetLastAndNextBookingTest {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static Item itemWithBookings, itemWithoutLastBookings, itemWithoutNextBookings, itemWithoutBookings;
    private static Booking i1LastBooking, i1NextBooking, i1WaitingBooking;
    private static Booking i2NextBooking1, i2NextBooking2, i2NextBookingRejected;
    private static Booking i3LastBooking1, i3LastBooking2, i3LastBookingCanceled, i3CurrentBooking;

    @BeforeEach
    public void setUp() {
        User owner = new User(null, "user1", "user1@mail.com", "1234", UserRole.USER, null);
        User booker = new User(null, "booker1", "booker1@mail.com", "1234", UserRole.USER, null);
        userRepository.saveAll(List.of(owner, booker));

        itemWithBookings = Item.builder()
                .name("item without last booking")
                .description("item 1")
                .available(true)
                .owner(owner)
                .build();
        itemWithoutLastBookings = Item.builder()
                .name("item without last booking")
                .description("item 2")
                .available(true)
                .owner(owner)
                .build();
        itemWithoutNextBookings = Item.builder()
                .name("item without next booking")
                .description("item 3")
                .available(true)
                .owner(owner)
                .build();
        itemWithoutBookings = Item.builder()
                .name("item without bookings")
                .description("item 4")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.saveAll(List.of(itemWithBookings, itemWithoutLastBookings,
                itemWithoutNextBookings, itemWithoutBookings));

        i1LastBooking = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemWithBookings, booker, Status.APPROVED);
        i1NextBooking = new Booking(null, LocalDateTime.now().plusDays(14), LocalDateTime.now().plusDays(28),
                itemWithBookings, booker, Status.APPROVED);
        i1WaitingBooking = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemWithBookings, booker, Status.WAITING);

        i2NextBooking1 = new Booking(null, LocalDateTime.now().plusDays(14), LocalDateTime.now().plusDays(28),
                itemWithoutLastBookings, booker, Status.APPROVED);
        i2NextBooking2 = new Booking(null, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20),
                itemWithoutLastBookings, booker, Status.APPROVED);
        i2NextBookingRejected = new Booking(null, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(14),
                itemWithoutLastBookings, booker, Status.REJECTED);

        i3LastBooking1 = new Booking(null, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(14),
                itemWithoutNextBookings, booker, Status.APPROVED);
        i3LastBooking2 = new Booking(null, LocalDateTime.now().minusDays(14), LocalDateTime.now().minusDays(10),
                itemWithoutNextBookings, booker, Status.APPROVED);
        i3LastBookingCanceled = new Booking(null, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3),
                itemWithoutNextBookings, booker, Status.CANCELED);
        i3CurrentBooking = new Booking(null, LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(3),
                itemWithoutNextBookings, booker, Status.APPROVED);

        bookingRepository.saveAll(List.of(i1LastBooking, i1NextBooking, i1WaitingBooking,
                i2NextBooking1, i2NextBooking2, i2NextBookingRejected,
                i3LastBooking1, i3LastBooking2, i3LastBookingCanceled));
    }

    @Test
    public void shouldGetLastBooking() {
        Map<Long, BookingShort> lastBookings = bookingService.getLastBooking(List.of(itemWithBookings.getId(), itemWithoutLastBookings.getId(),
                itemWithoutNextBookings.getId(), itemWithoutBookings.getId()));
        assertThat(lastBookings.size(), is(2));
        assertThat(lastBookings, hasKey(itemWithBookings.getId()));
        assertThat(lastBookings, hasKey(itemWithoutNextBookings.getId()));

        assertThat(lastBookings.get(itemWithBookings.getId()).getId(), is(i1LastBooking.getId()));
        assertThat(lastBookings.get(itemWithBookings.getId()).getItemId(), is(i1LastBooking.getItem().getId()));
        assertThat(lastBookings.get(itemWithBookings.getId()).getBookerId(), is(i1LastBooking.getBooker().getId()));

        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getId(), is(i3LastBooking2.getId()));
        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getItemId(), is(i3LastBooking2.getItem().getId()));
        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getBookerId(), is(i3LastBooking2.getBooker().getId()));

        bookingService.saveBooking(i3CurrentBooking);
        lastBookings = bookingService.getLastBooking(List.of(itemWithoutNextBookings.getId()));
        assertThat(lastBookings.size(), is(1));
        assertThat(lastBookings, hasKey(itemWithoutNextBookings.getId()));
        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getId(), is(i3CurrentBooking.getId()));
        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getItemId(), is(i3CurrentBooking.getItem().getId()));
        assertThat(lastBookings.get(itemWithoutNextBookings.getId()).getBookerId(), is(i3CurrentBooking.getBooker().getId()));
    }

    @Test
    public void shouldGetNextBooking() {
        Map<Long, BookingShort> nextBookings = bookingService.getNextBooking(List.of(itemWithBookings.getId(), itemWithoutLastBookings.getId(),
                itemWithoutNextBookings.getId(), itemWithoutBookings.getId()));
        assertThat(nextBookings.size(), is(2));
        assertThat(nextBookings, hasKey(itemWithBookings.getId()));
        assertThat(nextBookings, hasKey(itemWithoutLastBookings.getId()));

        assertThat(nextBookings.get(itemWithBookings.getId()).getId(), is(i1NextBooking.getId()));
        assertThat(nextBookings.get(itemWithBookings.getId()).getItemId(), is(i1NextBooking.getItem().getId()));
        assertThat(nextBookings.get(itemWithBookings.getId()).getBookerId(),is(i1NextBooking.getBooker().getId()));

        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getId(), is(i2NextBooking2.getId()));
        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getItemId(), is(i2NextBooking2.getItem().getId()));
        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getBookerId(), is(i2NextBooking2.getBooker().getId()));

        bookingRepository.deleteById(i2NextBooking2.getId());
        nextBookings = bookingService.getNextBooking(List.of(itemWithoutLastBookings.getId()));
        assertThat(nextBookings.size(), is(1));
        assertThat(nextBookings, hasKey(itemWithoutLastBookings.getId()));
        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getId(), is(i2NextBooking1.getId()));
        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getItemId(), is(i2NextBooking1.getItem().getId()));
        assertThat(nextBookings.get(itemWithoutLastBookings.getId()).getBookerId(), is(i2NextBooking1.getBooker().getId()));
    }
}
