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
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.comment.dto.CommentMapping;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapping commentMapping;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemGetDto> getItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        Item item = itemService.getItem(itemId, userId);
        return ResponseEntity.ok(itemMapper.itemToDto(item, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemGetDto>> getUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<Item> items = itemService.getUserItems(userId);
        return ResponseEntity.ok(itemMapper.itemsToDto(items));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemService.createItem(itemMapper.fromDto(itemDto, userId));
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@Validated @RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemService.updateItem(itemMapper.fromDto(itemDto, userId, itemId));
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text, @RequestHeader(USER_ID_HEADER) Long userId) {
        List<Item> items = itemService.searchItem(text, userId);
        return ResponseEntity.ok(items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentGetDto> addComment(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER) Long userId,
                                                    @Valid @RequestBody CommentCreateDto commentDto) {
        Comment comment = itemService.addComment(commentMapping.fromDto(commentDto, userId, itemId), userId, itemId);
        return ResponseEntity.ok(itemMapper.commentToDto(comment));
    }

    public Map<Long, List<ItemForRequestDto>> getItemsByRequestId(List<Long> requestIds) {
        Map<Long, List<Item>> map = itemService.getItemsByRequestId(requestIds);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(itemMapper::toRequestDto).collect(Collectors.toList())
                ));
    }
}
