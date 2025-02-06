package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final EntityManager entityManager;

    public User getUser(long userId) {
        User user = repository.findById(userId, User.class).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не был найден")
        );
        log.debug("Получен пользователь с id {}", userId);
        return user;
    }

    public List<User> getAllUsers() {
        log.debug("Получены все пользователи");
        return repository.findAll();
    }

    public User createUser(User user) {
        User created = repository.save(user);
        log.debug("Создан пользователь с id {}", created.getId());
        return created;
    }

    public User updateUser(User user) {
        User savedUser = getUser(user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(savedUser.getName());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(savedUser.getEmail());
        }
        log.debug("Изменен пользователь с id {}", user.getId());
        return repository.save(user);
    }

    public void deleteUser(long userId) {
        repository.deleteById(userId);
        log.debug("Удален пользователь с id {}", userId);
    }

     public UserShort getShortUser(Long userId) {
         return repository.findById(userId, UserShort.class).orElseThrow(
                 () -> new NotFoundException("Пользователь с id " + userId + " не был найден")
         );
     }

     public List<UserShort> getShortUsersByIds(List<Long> userIds) {
        return repository.findAllByIdIn(userIds);
     }

     public List<User> getUsersByIds(List<Long> userIds) {
        return repository.findAllById(userIds);
     }

     public User getUserReference(Long userId) {
        if (userId != null) {
            return entityManager.getReference(User.class, userId);
        } else {
            throw new NotFoundException("Id пользователя не может быть пустым");
        }
     }
}
