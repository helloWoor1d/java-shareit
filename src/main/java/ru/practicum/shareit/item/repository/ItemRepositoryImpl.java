package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items;

    public ItemRepositoryImpl() {
        items = new HashMap<>();
    }

    @Override
    public Item get(long itemId, long userId) {
        return items.values().stream()
                .flatMap(l -> l.stream()
                        .filter(i -> i.getId() == itemId)
                )
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не была найдена у пользователя с id " + userId));
        }

    @Override
    public List<Item> getUserItems(long userId) {
        return new ArrayList<>(items.get(userId));
    }

    @Override
    public Item create(Item item) {
        if (items.containsKey(item.getOwner())) {
            items.get(item.getOwner()).add(item);
            return item;
        }
        items.put(item.getOwner(), new ArrayList<>(List.of(item)));
        return item;
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getOwner())) {
            Item updatedItem = items.get(item.getOwner()).stream()
                    .filter(i -> i.getId().equals(item.getId()))
                    .findFirst().orElseThrow(() -> new ItemNotFoundException("Вещь с id " + item.getId() + " не была найдена у пользователя с id " + item.getOwner()));
            if (item.getName() != null) {
                updatedItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                updatedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                updatedItem.setAvailable(item.getAvailable());
            }
            return updatedItem;
        }
        throw new ItemNotFoundException("Вещь с id " + item.getId() + " не была найдена у пользователя с id " + item.getOwner());
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .flatMap(l -> l.stream()
                        .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                                i.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                i.getAvailable()))
                .collect(Collectors.toList());
    }
}
