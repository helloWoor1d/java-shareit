package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUser(long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    void emailIsValid(String email, Long userId);
}
