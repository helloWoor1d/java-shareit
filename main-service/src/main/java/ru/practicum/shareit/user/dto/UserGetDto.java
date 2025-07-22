package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserGetDto {
    private String name;
    private String email;
    private String avatarUrl;
}
