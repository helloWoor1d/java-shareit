package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private final EntityManager entityManager;
    private static User u1, u2;

    @BeforeEach
    public void setUp() {
        u1 = User.builder()
                .name("User 1")
                .email("emailUser1@gmail.com")
                .password("12345678")
                .build();

        u2 = User.builder()
                .name("User 2")
                .email("emailUser2@gmail.com")
                .password("12345678")
                .build();
    }

    @Test
    public void shouldGetCreateUpdateDeleteUser() {
        User user = userService.createUser(u1);
        assertThat(user, hasProperty("id"));
        assertThat(user, samePropertyValuesAs(u1));

        assertThat(user, equalTo(userService.getUser(user.getId())));

        u1.setId(user.getId());
        u1.setName("Updated name");
        u1.setEmail("Updated email");
        User updatedUser = userService.updateUser(u1);
        assertThat(updatedUser, equalTo(u1));

        User userForUpdate = User.builder().id(u1.getId()).build();
        updatedUser = userService.updateUser(userForUpdate);
        assertThat(updatedUser, equalTo(u1));

        userForUpdate = User.builder().id(u1.getId()).name("").email("").build();
        updatedUser = userService.updateUser(userForUpdate);
        assertThat(updatedUser, equalTo(u1));

        userService.deleteUser(u1.getId());
        Exception ex = assertThrows(NotFoundException.class,
                () -> userService.getUser(u1.getId()));
        assertThat(ex.getMessage(),
                equalTo(String.format("Пользователь с id %d не был найден", u1.getId())));
    }

    @Test
    public void shouldGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertThat(users, is(empty()));

        userService.createUser(u1);
        userService.createUser(u2);
        users = userService.getAllUsers();

        assertThat(users, hasSize(2));
        assertThat(users, hasItems(u1, u2));
    }

    @Test
    public void shouldGetShortUsers() {
        userService.createUser(u1);
        userService.createUser(u2);

        UserShort shortUser = userService.getShortUser(u1.getId());
        assertThat(shortUser, hasProperty("id", equalTo(u1.getId())));
        assertThat(shortUser, hasProperty("name", equalTo(u1.getName())));

        Exception ex = assertThrows(NotFoundException.class,
                () -> userService.getShortUser(256L));
        assertThat(ex.getMessage(), equalTo("Пользователь с id 256 не был найден"));

        List<UserShort> shorts = userService.getShortUsersByIds(List.of(u1.getId(), u2.getId()));
        assertThat(shorts, hasSize(2));
    }

    @Test
    public void shouldGetReference() {
        Exception ex = assertThrows(NotFoundException.class,
                () -> userService.getUserReference(null));
        assertThat(ex.getMessage(), equalTo("Id пользователя не может быть пустым"));

        userService.createUser(u1);
        entityManager.clear();
        User reference = userService.getUserReference(u1.getId());

        assertThat(Hibernate.isInitialized(reference), is(false));
        assertThat(reference, hasProperty("id", equalTo(u1.getId())));

        reference.getEmail();
        assertThat(Hibernate.isInitialized(reference), is(true));
    }
}
