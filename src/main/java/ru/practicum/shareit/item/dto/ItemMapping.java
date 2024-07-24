package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapping {
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto, long userId);


    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto, long userId, long itemId);

    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "comments")
    ItemGetDto toGetDto(Item item, BookingShort lastBooking, BookingShort nextBooking, List<CommentGetDto> comments);
}
