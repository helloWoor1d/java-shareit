package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users;

    public UserRepositoryImpl() {
        users = new HashMap<>();
    }

    @Override
    public User getUser(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public void emailIsValid(String email, Long userId) {
        Optional<User> userWithSameEmail = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        if (userWithSameEmail.isPresent()) {
            if (userId != null && userWithSameEmail.get().getId().equals(userId)) {
                return;
            }
            throw new ValidationException("Этот email уже используется");
        }
    }
}
