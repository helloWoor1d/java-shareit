package ru.practicum.shareit.booking.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserShort;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper extends BookingMappingImpl {
    private final ItemService itemService;
    private final UserService userService;

    public List<BookingGetDto> listBookingsToDto(List<Booking> bookings) {
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
                    return toDto(booking, booker, item);
                }).collect(Collectors.toList());
        return bookingsDto;
    }
}
