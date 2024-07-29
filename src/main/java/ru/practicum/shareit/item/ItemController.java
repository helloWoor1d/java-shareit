package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.comment.dto.CommentMapping;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.ItemMapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.UserShort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapping itemMapping;
    private final CommentMapping commentMapping;
    private final BookingService bookingService;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemGetDto> getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Item item = itemService.getItem(itemId, userId);
        return ResponseEntity.ok(itemToDto(item, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemGetDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<Item> items = itemService.getUserItems(userId);
        return ResponseEntity.ok(itemsToDto(items));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.createItem(itemMapping.fromDto(itemDto, userId));
        return ResponseEntity.ok(itemMapping.toDto(item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@Validated @RequestBody ItemDto itemDto, @PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.updateItem(itemMapping.fromDto(itemDto, userId, itemId));
        return ResponseEntity.ok(itemMapping.toDto(item));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId) {
        List<Item> items = itemService.searchItem(text, userId);
        return ResponseEntity.ok(items.stream()
                .map(itemMapping::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentGetDto> addComment(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody CommentCreateDto commentDto) {
        Comment comment = itemService.addComment(commentMapping.fromDto(commentDto, userId, itemId), userId, itemId);
        return ResponseEntity.ok(commentToDto(comment));
    }

    private ItemGetDto itemToDto(Item item, Long userId) {
        List<Comment> comments = itemService.getItemComments(List.of(item.getId()));
        List<CommentGetDto> itemComments = commentsToDto(comments);

        ItemGetDto dto;
        if (item.getOwner().getId().equals(userId)) {
            Map<Long, BookingShort> lastBookings = bookingService.getLastBooking(new ArrayList<>(List.of(item.getId())));
            Map<Long, BookingShort> nextBookings = bookingService.getNextBooking(new ArrayList<>(List.of(item.getId())));
            dto = itemMapping.toGetDto(item, lastBookings.get(item.getId()), nextBookings.get(item.getId()), itemComments);
        } else {
            dto = itemMapping.toGetDto(item, null, null, itemComments);
        }
        return dto;
    }

    private List<ItemGetDto> itemsToDto(List<Item> items) {
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
                    return itemMapping.toGetDto(i, lastBookings.getOrDefault(i.getId(), null),
                            nextBookings.getOrDefault(i.getId(), null), itemComments);
                })
                .sorted(Comparator.nullsLast(Comparator.comparing(item -> {
                    BookingShort bookingLast = item.getLastBooking();
                    return bookingLast == null ? 3 : -1;
                }))).collect(Collectors.toList());
        return itemsDto;
    }

    private CommentGetDto commentToDto(Comment comment) {
        UserShort user = itemService.getUserShorts(List.of(comment.getAuthor().getId())).get(0);
        return commentMapping.toDto(comment, user.getName());
    }

    private List<CommentGetDto> commentsToDto(List<Comment> comments) {
        List<Long> usersId = comments.stream().map(c -> c.getAuthor().getId()).collect(Collectors.toList());
        List<UserShort> users = itemService.getUserShorts(usersId);

        List<CommentGetDto> itemComments = comments.stream()
                .map(comment -> {
                    UserShort user = users.stream()
                            .filter(u -> u.getId().equals(comment.getAuthor().getId()))
                            .findFirst().orElseThrow();
                    return commentMapping.toDto(comment, user.getName());
                })
                .collect(Collectors.toList());
        return itemComments;
    }
}
