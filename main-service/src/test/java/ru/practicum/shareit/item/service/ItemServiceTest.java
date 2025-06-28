package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private final EntityManager entityManager;

    private static Item i1, i2;
    private static User u1, u2;
    private static ItemRequest r1, r2;

    @BeforeEach
    public void setUp() {
        u1 = User.builder()
                .name("User 1")
                .email("user1@gmail.com")
                .password("12345678")
                .build();
        u2 = User.builder()
                .name("User 2")
                .email("user2@gmail.com")
                .password("12345678")
                .build();
        i1 = Item.builder()
                .name("Item 1")
                .description("description for item 1")
                .available(true)
                .owner(u1)
                .build();
        i2 = Item.builder()
                .name("Item 2")
                .description("description for item 2")
                .available(true)
                .owner(u1)
                .build();
        r1 = ItemRequest.builder()
                .description("description for request 1")
                .requester(u1)
                .created(LocalDateTime.now())
                .build();
        r2 = ItemRequest.builder()
                .description("description for request 2")
                .requester(u2)
                .created(LocalDateTime.now())
                .build();

        userService.createUser(u1);
        userService.createUser(u2);
        requestService.createRequest(r1, u1.getId());
        requestService.createRequest(r2, u2.getId());
    }

    @Test
    public void shouldCreateAndUpdateItem() {
        i1.setOwner(User.builder()
                .id(18L).build());
        Exception ex = assertThrows(NotFoundException.class,
                () -> itemService.createItem(i1),
                "Ожидается выброс исключения при попытке создать вещь с несуществующим владельцем");
        assertThat(ex.getMessage(), is("Пользователь с id 18 не был найден"));

        i1.setOwner(u1);
        Item item = itemService.createItem(i1);
        assertThat(item, is(i1));

        i2.setRequest(r1);
        item = itemService.createItem(i2);
        assertThat(item, is(i2));

        ex = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(Item.builder().id(33L).owner(u1).build()),
                "Ожидается выброс исключения при попытке обновить вещь с несуществующим id");
        assertThat(ex.getMessage(), is("Вещь с id 33 не найдена"));

        item = Item.builder()
                .id(i1.getId())
                .description("updated description")
                .owner(u1)
                .available(false)
                .build();
        i1.setDescription("updated description");
        i1.setAvailable(false);
        Item updatedItem1 = itemService.updateItem(item);
        assertThat(updatedItem1, is(i1));

        item = Item.builder()
                .id(i1.getId())
                .name("updated name")
                .owner(u1)
                .build();
        updatedItem1 = itemService.updateItem(item);
        assertThat(updatedItem1, samePropertyValuesAs(i1));
    }

    @Test
    public void shouldGetDeleteItems() {
        itemService.createItem(i1);
        itemService.createItem(i2);

        Item item = itemService.getItem(i1.getId(), u1.getId());
        assertThat(item, is(i1));

        assertThat(itemService.getUserItems(u2.getId()), is(empty()));
        assertThat(itemService.getUserItems(u1.getId()), hasSize(2));
        assertThat(itemService.getUserItems(u1.getId()), hasItems(i1, i2));

        itemService.deleteItem(i1.getId());
        Exception ex = assertThrows(NotFoundException.class,
                () -> itemService.getItem(i1.getId(), u2.getId()));
        assertThat(ex.getMessage(), is("Вещь с id " + i1.getId() + " не была найдена"));
    }

    @Test
    public void shouldSearchItem() {
        itemService.createItem(i1);
        itemService.createItem(i2);

        List<Item> foundItems = itemService.searchItem("Item 3456", u1.getId());
        assertThat(foundItems, is(empty()));

        foundItems = itemService.searchItem(null, u1.getId());
        assertThat(foundItems, is(empty()));

        foundItems = itemService.searchItem("", u1.getId());
        assertThat(foundItems, is(empty()));

        foundItems = itemService.searchItem("Item 1", u2.getId());
        assertThat(foundItems, hasSize(1));
        assertThat(foundItems, hasItems(i1));

        foundItems = itemService.searchItem("Item", u2.getId());
        assertThat(foundItems, hasSize(2));
        assertThat(foundItems, hasItems(i1, i2));
    }

    @Test
    public void shouldGetShortItems() {
        itemService.createItem(i1);
        itemService.createItem(i2);

        ItemShort shortItem = itemService.getShortItem(i2.getId());
        assertThat(shortItem, hasProperty("id", is(i2.getId())));
        assertThat(shortItem, hasProperty("name", is(i2.getName())));
        assertThat(shortItem, hasProperty("ownerId", is(i2.getOwner().getId())));

        Exception ex = assertThrows(NotFoundException.class,
                () -> itemService.getShortItem(7654L));
        assertThat(ex.getMessage(), is("Вещь с id 7654 не была найдена"));

        List<ItemShort> shorts = itemService.getShortItemsByIds(List.of(i2.getId(), i1.getId()));
        assertThat(shorts, hasSize(2));
    }

    @Test
    public void shouldGetShortUser() {
        userService.createUser(u1);
        userService.createUser(u2);

        List<UserShort> shortUsers = itemService.getUserShorts(List.of(u1.getId(), u2.getId()));
        assertThat(shortUsers, hasSize(2));
    }

    @Test
    public void shouldGetItemsByRequestId() {
        i1.setRequest(r1);
        i2.setRequest(r1);
        itemService.createItem(i1);
        itemService.createItem(i2);

        Map<Long, List<Item>> itemsByRequests = itemService.getItemsByRequestId(List.of(r1.getId(), r2.getId()));
        assertThat(itemsByRequests, hasKey(r1.getId()));
        assertThat(itemsByRequests, not(hasKey(r2.getId())));
        assertThat(itemsByRequests.get(r1.getId()), hasSize(2));
        assertThat(itemsByRequests.get(r1.getId()), hasItems(i1, i2));
    }

    @Test
    public void shouldGetReference() {
        Exception ex = assertThrows(NotFoundException.class,
                () -> itemService.getItemReference(null));
        assertThat(ex.getMessage(), is("Id вещи не должен быть пустым"));

        itemService.createItem(i1);
        entityManager.clear();
        Item reference = itemService.getItemReference(i1.getId());

        assertThat(Hibernate.isInitialized(reference), is(false));
        assertThat(reference, hasProperty("id", is(i1.getId())));

        reference.getName();
        assertThat(Hibernate.isInitialized(reference), is(true));
    }
}
