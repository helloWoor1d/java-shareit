package ru.practicum.shareit.booking.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserShort;

@Mapper(componentModel = "spring", uses = {ItemService.class, UserService.class})
public interface BookingMapping {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "item", source = "dto.itemId")
    @Mapping(target = "booker", source = "bookerId")
    Booking fromDto(BookingCreateDto dto, Long bookerId);

    @Mapping(target = "booker", source = "booking.booker.id")
    @Mapping(target = "item", source = "booking.item.id")
    BookingGetDto toDto(Booking booking);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    BookingGetDto toDto(Booking booking, UserShort user, ItemShort item);
}
