package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.YandexStorageService;
import ru.practicum.shareit.user.model.SecurityUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;
import ru.practicum.shareit.user.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private final YandexStorageService yandexStorageService;

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
        user.setPassword(
                passwordEncoder.encode(user.getPassword()));
        User created = repository.save(user);
        log.debug("Создан пользователь с id {}", created.getId());
        return created;
    }

    public User updateUser(User user) {
        User savedUser = getUser(user.getId());
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(savedUser.getPassword());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(savedUser.getName());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(savedUser.getEmail());
        }
        log.debug("Изменен пользователь с id {}", user.getId());
        return repository.save(user);
    }

    public String uploadUserAvatar(Long userId, MultipartFile file) throws IOException {
        User user = repository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        String avatarUrl = yandexStorageService.uploadUserAvatar(userId, file);
        user.setAvatarUrl(avatarUrl);
        repository.save(user);

        log.debug("Добавлена фотография пользователя с id {}", userId);
        return avatarUrl;
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

     public SecurityUser getUserPrivate(String email) {
        log.debug("Запрос на получение пользователя с email {} от сервиса авторизации", email);
        return repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
     }
}
