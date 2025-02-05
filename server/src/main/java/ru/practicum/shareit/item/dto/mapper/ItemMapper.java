package ru.practicum.shareit.item.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.comment.dto.CommentMapping;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ItemMapper extends ItemMappingImpl {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final CommentMapping commentMapping;

    public ItemGetDto itemToDto(Item item, Long userId) {
        List<Comment> comments = itemService.getItemComments(List.of(item.getId()));
        List<CommentGetDto> itemComments = commentsToDto(comments);

        ItemGetDto dto;
        if (item.getOwner().getId().equals(userId)) {
            Map<Long, BookingShort> lastBookings = bookingService.getLastBooking(new ArrayList<>(List.of(item.getId())));
            Map<Long, BookingShort> nextBookings = bookingService.getNextBooking(new ArrayList<>(List.of(item.getId())));
            dto = toGetDto(item, lastBookings.get(item.getId()), nextBookings.get(item.getId()), itemComments);
        } else {
            dto = toGetDto(item, null, null, itemComments);
        }
        return dto;
    }

    public List<ItemGetDto> itemsToDto(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, BookingShort> lastBookings = bookingService.getLastBooking(itemIds);
        Map<Long, BookingShort> nextBookings = bookingService.getNextBooking(itemIds);

        List<Comment> comments = itemService.getItemComments(itemIds);
        List<CommentGetDto> commentsDto = commentsToDto(comments);

        List<ItemGetDto> itemsDto = items.stream()
                .map(i -> {
                    List<CommentGetDto> itemComments = commentsDto.stream()
                            .filter(c -> c.getItemId().equals(i.getId()))
                            .collect(Collectors.toList());
                    return toGetDto(i, lastBookings.getOrDefault(i.getId(), null),
                            nextBookings.getOrDefault(i.getId(), null), itemComments);
                })
                .sorted(Comparator.nullsLast(Comparator.comparing(item -> {
                    BookingShort bookingLast = item.getLastBooking();
                    return bookingLast == null ? 3 : -1;
                }))).collect(Collectors.toList());
        return itemsDto;
    }

    public CommentGetDto commentToDto(Comment comment) {
        User user = itemService.getUsersByIds(List.of(comment.getAuthor().getId())).get(0);
        return commentMapping.toDto(comment, user.getName());
    }

    private List<CommentGetDto> commentsToDto(List<Comment> comments) {
        List<Long> usersId = comments.stream().map(c -> c.getAuthor().getId()).collect(Collectors.toList());
        List<User> users = itemService.getUsersByIds(usersId);

        List<CommentGetDto> itemComments = comments.stream()
                .map(comment -> {
                    User user = users.stream()
                            .filter(u -> u.getId().equals(comment.getAuthor().getId()))
                            .findFirst().orElseThrow();
                    return commentMapping.toDto(comment, user.getName());
                })
                .collect(Collectors.toList());
        return itemComments;
    }
}
