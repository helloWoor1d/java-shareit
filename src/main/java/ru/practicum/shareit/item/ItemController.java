package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapping;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapping itemMapping;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Item item = itemService.getItem(itemId, userId);
        return ResponseEntity.ok(itemMapping.toDto(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> items = itemService.getUserItems(userId).stream()
                .map(itemMapping::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
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
}
