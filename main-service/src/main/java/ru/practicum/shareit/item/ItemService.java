package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    private final YandexStorageService yandexStorageService;

    public Item getItem(long itemId, long userId) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId, Item.class).orElseThrow(
                () -> new NotFoundException("Вещь с id " + itemId + " не была найдена")
        );
        log.debug("Получена вещь с id {}, пользователя с id {}", itemId, userId);
        return item;
    }

    public List<Item> getUserItems(long userId) {
        userService.getUser(userId);
        log.debug("Получен список вещей пользователя с id {}", userId);
        return itemRepository.findAllByOwnerId(userId);
    }

    public Item createItem(Item item) {
        userService.getUser(item.getOwner().getId());
        if (item.getRequest() != null) itemRequestService.getRequest(item.getRequest().getId(), item.getOwner().getId());

        Item created = itemRepository.save(item);
        log.debug("Добавлена вещь с id {} пользователем {}", created.getId(), item.getOwner());
        return created;
    }

    public Item updateItem(Item item) {
        if (item.getOwner() != null) userService.getUser(item.getOwner().getId());

        Item savedItem = itemRepository.findByIdAndOwnerId(item.getId(), item.getOwner().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + item.getId() + " не найдена"));
        if (item.getName() == null) {
            item.setName(savedItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(savedItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(savedItem.getAvailable());
        }
        log.debug("Изменена вещь с id {}", item.getId());
        return itemRepository.save(item);
    }

    public void deleteItem(long itemId) {
        log.debug("Удалена вещь с id {}", itemId);
        itemRepository.deleteById(itemId);
    }

    public List<Item> searchItem(String text, long userId) {
        userService.getUser(userId);
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    public Comment addComment(Comment comment, Long userId, Long itemId) {
        List<Booking> userItemBookings = getPastItemBookings(userId, itemId);
        if (userItemBookings.isEmpty()) {
            throw new BadOperationException("Комментарии могут оставлять пользователи, бравшие вещь в аренду, и только по истечении срока аренды");
        }
        comment.setCreated(LocalDateTime.now());
        Comment createdComment = commentRepository.save(comment);
        log.debug("Добавлен комментарий для вещи с id {}, пользователем с id {}", itemId, userId);
        return createdComment;
    }

    public List<Comment> getItemComments(List<Long> itemId) {
        return commentRepository.findAllByItemIdIn(itemId);
    }

    public List<UserShort> getUserShorts(List<Long> userId) {
        return userService.getShortUsersByIds(userId);
    }

    public List<User> getUsersByIds(List<Long> userId) {
        return userService.getUsersByIds(userId);
    }

    public ItemShort getShortItem(Long itemId) {
        return itemRepository.findById(itemId, ItemShort.class).orElseThrow(
                () -> new NotFoundException("Вещь с id " + itemId + " не была найдена")
        );
    }

    public List<ItemShort> getShortItemsByIds(List<Long> itemIds) {
        return itemRepository.findAllByIdIn(itemIds);
    }

    public Map<Long, List<Item>> getItemsByRequestId(List<Long> requestId) {
        log.debug("Получен список вещей, созданных по запросам с id {}", requestId);
        List<Item> items = itemRepository.findAllByRequestIdIn(requestId);
        return items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
    }

    @Named("getItemRefById")
    public Item getItemReference(Long itemId) {
        if (itemId != null) {
            return entityManager.getReference(Item.class, itemId);
        } else {
            throw new NotFoundException("Id вещи не должен быть пустым");
        }
    }

    private List<Booking> getPastItemBookings(Long userId, Long itemId) {
        return itemRepository.getUserItemBookings(userId, itemId, LocalDateTime.now());
    }

    public String uploadFile(long itemId, MultipartFile file) throws IOException { // toDo: при изменении фотографии, старая удаляется
        Item item = itemRepository.findById(itemId, Item.class).orElseThrow(
                () -> new NotFoundException("Вещь с id " + itemId + " не была найдена"));

        String imageUrl = yandexStorageService.uploadItemImage(itemId, file);
        item.setImageUrl(imageUrl);
        itemRepository.save(item);
        return imageUrl;
    }

    public Item getItemWithImage(Long itemId) {
        return itemRepository.findById(itemId, Item.class)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
    }

    public List<Item> getAllItemsWithImage() {
        PageRequest page = PageRequest.of(0, 10);
        log.debug("Получена вся техника вместе с фотографиями");
        return itemRepository.findAll(page).stream().toList();
    }
}
