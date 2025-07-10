package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserRole;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMappingTest {
    @InjectMocks
    private BookingMappingImpl bookingMapping;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        user = new User(3L, "name", "email@email.com", "12345", UserRole.USER);
    }

    @Test
    public void fromDtoTest() {
        Booking booking = bookingMapping.fromDto(null, null);
        assertThat(booking, is(nullValue()));

        when(userService.getUserReference(user.getId())).thenReturn(user);
        booking = bookingMapping.fromDto(null, user.getId());
        assertThat(booking, is(notNullValue()));
        assertThat(booking.getId(), is(nullValue()));
        assertThat(booking.getBooker(), is(notNullValue()));
        assertThat(booking.getBooker().getId(), is(user.getId()));
    }

    @Test
    public void toDtoTest() {
        BookingGetDto dto = bookingMapping.toDto(null);
        assertThat(dto, is(nullValue()));

        dto = bookingMapping.toDto(new Booking());
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(nullValue()));
        assertThat(dto.getBooker(), is(nullValue()));
        assertThat(dto.getItem(), is(nullValue()));

        dto = bookingMapping.toDto(Booking.builder().booker(new User()).item(new Item()).build());
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(nullValue()));
        assertThat(dto.getBooker(), is(nullValue()));
        assertThat(dto.getItem(), is(nullValue()));

        dto = bookingMapping.toDto(null, null, null);
        assertThat(dto, is(nullValue()));
    }
}
