package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.comment.dto.CommentGetDto;

import java.util.List;

@Data
@Builder
public class ItemGetDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShort lastBooking;

    private BookingShort nextBooking;

    private List<CommentGetDto> comments;

    private String imageUrl;
}
