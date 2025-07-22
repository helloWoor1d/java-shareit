package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapping {
    UserDto toDto(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    User fromDto(UserDto dto);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "id", source = "userId")
    User fromDto(UserDto dto, long userId);

    UserGetDto toGetDto(User user);
}
