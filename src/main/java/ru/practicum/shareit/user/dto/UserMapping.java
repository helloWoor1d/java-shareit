package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapping {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    public static User fromDto(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail()).build();
    }

    public static User fromDto(UserDto dto, long userId) {
        return User.builder()
                .id(userId)
                .name(dto.getName())
                .email(dto.getEmail()).build();
    }
}
