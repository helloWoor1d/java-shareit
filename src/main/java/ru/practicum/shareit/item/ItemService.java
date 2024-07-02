package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@Slf4j
public class ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private static long id = 1;

    public ItemService(@Autowired ItemRepository repository, @Autowired UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Item getItem(long itemId, long userId) {
        checkUserExistence(userId);
        log.debug("Получена вещь с id {}, пользователя с id {}", itemId, userId);
        return repository.get(itemId, userId);
    }

    public List<Item> getUserItems(long userId) {
        log.debug("Получен список вещей пользователя с id {}", userId);
        return repository.getUserItems(userId);
    }

    public Item createItem(Item item) {
        checkUserExistence(item.getOwner());
        item.setId(generateId());
        log.debug("Добавлена вещь с id {} пользователем с id {}", item.getId(), item.getOwner());
        return repository.create(item);
    }

    public Item updateItem(Item item) {
        checkUserExistence(item.getOwner());
        log.debug("Изменена вещь с id {}", item.getId());
        return repository.update(item);
    }

    public void deleteItem(long itemId) {
        log.debug("Удалена вещь с id {}", itemId);
        repository.delete(itemId);
    }

    public List<Item> searchItem(String text, long userId) {
        if (userService.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не был найден");
        }
        return repository.searchItem(text);
    }

    private long generateId() {
        return id++;
    }

    private void checkUserExistence(long userId) {
        try {
            userService.getUser(userId);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Ошибка! Пользователь с id " + userId + " не был найден");
        }
    }
}
