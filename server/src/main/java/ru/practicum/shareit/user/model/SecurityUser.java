package ru.practicum.shareit.user.model;

public interface SecurityUser {
    Long getId();

    String getEmail();

    String getPassword();

    UserRole getRole();
}
