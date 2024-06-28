package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapping;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Autowired ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Item item = itemService.getItem(itemId, userId);
        return ItemMapping.toDto(item);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUserItems(userId).stream()
                .map(ItemMapping::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto createItem(@Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.createItem(ItemMapping.fromDto(itemDto, userId));
        return ItemMapping.toDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated @RequestBody ItemDto itemDto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.updateItem(ItemMapping.fromDto(itemDto, userId, itemId));
        return ItemMapping.toDto(item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.searchItem(text, userId);
    }
}
