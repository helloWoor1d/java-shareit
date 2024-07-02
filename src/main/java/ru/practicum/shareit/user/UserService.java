package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private long id = 1;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(long userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с таким id не был найден");
        }
        log.debug("Получен пользователь с id {}", userId);
        return user;
    }

    public List<User> getAllUsers() {
        log.debug("Получены все пользователи");
        return userRepository.getAllUsers();
    }

    public User createUser(User user) {
        userRepository.emailIsValid(user.getEmail(), user.getId());
        user.setId(generateId());
        log.debug("Создан пользователь с id {}", user.getId());
        return userRepository.createUser(user);
    }

    public User updateUser(User user) {
        if (user.getEmail() != null) userRepository.emailIsValid(user.getEmail(), user.getId());
        log.debug("Изменен пользователь с id {}", user.getId());
        return userRepository.updateUser(user);
    }

    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
        log.debug("Удален пользователь с id {}", userId);
    }

    private long generateId() {
        return id++;
    }
}
