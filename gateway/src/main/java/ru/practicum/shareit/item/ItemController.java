package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        log.info("Запрос от пользователя {} на получение вещи с id {}", userId, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Запрос от пользователя {} на получение всех его вещей", userId);
        return itemClient.getUserItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос от пользователя {} на создание вещи {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Validated @RequestBody ItemDto itemDto) {
        log.info("Запрос от пользователя {} на изменение вещи с id {}", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        log.info("Запрос на изменение вещи с id {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam String text) {
        log.info("Запрос от пользователя {} на поиск вещей, соответсвующих \"{}\"", userId, text);
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос от пользователя {} на добавление комментария к вещи с id {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
