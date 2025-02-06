package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.mapper.BookingMappingImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadOperationException;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = BookingController.class)
@Import({BookingMappingImpl.class, BookingMapper.class})
public class BookingControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private final BookingService bookingService;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ItemService itemService;

    private final BookingMapper bookingMapper;
    private Booking b1;
    private User owner, user;
    private Item item;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        user = User.builder().id(2L).name("user1").email("user1@mail.ru").build();
        item = Item.builder().id(1L).name("Item 1").description("description for item 1").available(true).owner(owner).build();
        b1 = Booking.builder().id(1L).item(item).booker(user).status(Status.APPROVED).build();
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        when(bookingService.saveBooking(any())).thenReturn(b1);
        when(itemService.getItemReference(anyLong())).thenReturn(item);
        when(userService.getUserReference(anyLong())).thenReturn(user);

        MockHttpServletResponse response = performCreateBooking(LocalDateTime.now().minusYears(2), LocalDateTime.now().plusDays(2), item.getId(), user.getId());
        assertThat(response.getStatus(), is(400));
        verify(bookingService, Mockito.times(0)).saveBooking(any());

        response = performCreateBooking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(16), item.getId(), user.getId());
        assertThat(response.getStatus(), is(200));
        BookingGetDto content = mapper.readValue(response.getContentAsString(), BookingGetDto.class);
        assertThat(content.getId(), is(b1.getId()));
        verify(bookingService, Mockito.times(1)).saveBooking(any());
    }

    @Test
    public void shouldGetErrorIfItemNotAvailable() throws Exception {
        when(bookingService.saveBooking(any())).thenThrow(new BadOperationException("Item not available"));

        MockHttpServletResponse response = performCreateBooking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(16), item.getId(), user.getId());
        assertThat(response.getStatus(), is(400));
        ErrorResponse error = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(error.getError(), is("Item not available"));
    }

    @Test
    public void shouldGetErrorIfBookingStartDateAfterEnd() throws Exception {
        when(bookingService.saveBooking(any())).thenThrow(new ValidationException("Booking start date after end"));

        MockHttpServletResponse response = performCreateBooking(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(16), item.getId(), user.getId());
        assertThat(response.getStatus(), is(400));
        ErrorResponse error = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(error.getError(), is("Booking start date after end"));
    }

    public MockHttpServletResponse performCreateBooking(LocalDateTime start, LocalDateTime end, long itemId, long userId) throws Exception {
        BookingCreateDto dto = BookingCreateDto.builder().start(start).end(end).itemId(itemId).build();
        MvcResult result = mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(b1);

        MockHttpServletResponse response = performApproveBooking(b1.getId(), true, user.getId());
        assertThat(response.getStatus(), is(200));
        BookingGetDto content = mapper.readValue(response.getContentAsString(), BookingGetDto.class);
        assertThat(content.getId(), is(b1.getId()));

        verify(bookingService, Mockito.times(1)).approveBooking(anyLong(), anyBoolean(), anyLong());
    }

    public MockHttpServletResponse performApproveBooking(long bookingId, boolean approved, long userId) throws Exception {
        MvcResult result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(b1);

        MockHttpServletResponse response = performGetBooking(b1.getId(), user.getId());
        assertThat(response.getStatus(), is(200));
        BookingGetDto content = mapper.readValue(response.getContentAsString(), BookingGetDto.class);
        assertThat(content.getId(), is(b1.getId()));

        verify(bookingService, Mockito.times(1)).getBooking(anyLong(), anyLong());
    }

    public MockHttpServletResponse performGetBooking(long bookingId, long userId) throws Exception {
        MvcResult result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetUserBookings() throws Exception {
        when(bookingService.getUserBookings(2L, State.ALL)).thenReturn(List.of(b1));
        when(bookingService.getUserBookings(2L, State.WAITING)).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = performGetUserBookings(user.getId(), "meow");
        assertThat(response.getStatus(), is(400));
        ErrorResponse error = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(error.getError(), is("Unknown state: meow"));

        response = performGetUserBookings(user.getId(), "WAITING");
        assertThat(response.getStatus(), is(200));
        List<BookingGetDto> content = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertThat(content.size(), is(0));
        verify(bookingService, Mockito.times(1)).getUserBookings(2L, State.WAITING);

        response = performGetUserBookings(user.getId(), "");
        assertThat(response.getStatus(), is(200));
        content = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertThat(content.size(), is(1));
        verify(bookingService, Mockito.times(1)).getUserBookings(2L, State.ALL);
    }

    public MockHttpServletResponse performGetUserBookings(long userId, String state) throws Exception {
        MvcResult result = mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(1L, State.ALL)).thenReturn(List.of(b1));
        when(bookingService.getOwnerBookings(1L, State.WAITING)).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = performGetOwnerBookings(owner.getId(), "meow");
        assertThat(response.getStatus(), is(400));
        ErrorResponse error = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(error.getError(), is("Unknown state: meow"));
        verify(bookingService, Mockito.times(0)).getUserBookings(anyLong(), any());

        response = performGetOwnerBookings(owner.getId(), "WAITING");
        assertThat(response.getStatus(), is(200));
        List<BookingGetDto> content = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertThat(content.size(), is(0));
        verify(bookingService, Mockito.times(1)).getOwnerBookings(1L, State.WAITING);

        response = performGetOwnerBookings(owner.getId(), "");
        assertThat(response.getStatus(), is(200));
        content = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertThat(content.size(), is(1));
        verify(bookingService, Mockito.times(1)).getOwnerBookings(1L, State.ALL);
    }

    public MockHttpServletResponse performGetOwnerBookings(long ownerId, String state) throws Exception {
        MvcResult result = mvc.perform(get("/bookings/owner")
                .header(USER_ID_HEADER, ownerId)
                .param("state", state)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        return result.getResponse();
    }
}
