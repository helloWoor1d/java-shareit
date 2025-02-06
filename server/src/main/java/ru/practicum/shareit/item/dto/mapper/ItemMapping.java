package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserService.class, ItemRequestService.class})
public interface ItemMapping {
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", source = "itemDto.requestId")
    Item fromDto(ItemDto itemDto, long userId);

    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", source = "itemDto.requestId")
    Item fromDto(ItemDto itemDto, long userId, long itemId);

    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "comments")
    ItemGetDto toGetDto(Item item, BookingShort lastBooking, BookingShort nextBooking, List<CommentGetDto> comments);

    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemForRequestDto toRequestDto(Item item);
}
