package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item get(long itemId, long userId);

    List<Item> getUserItems(long userId);

    Item create(Item item);

    Item update(Item item);

    void delete(long id);

    List<Item> searchItem(String text);
}
