package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.SecurityUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserShort;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    <T> Optional<T> findById(Long id, Class<T> type);

    List<UserShort> findAllByIdIn(List<Long> userIds);

    Optional<SecurityUser> findByEmail(String email);
}