package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapping;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return UserMapping.toDto(
                userService.getUser(userId)
        );
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapping::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        User user = userService.createUser(UserMapping.fromDto(userDto));
        return UserMapping.toDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {
        User user = userService.updateUser(UserMapping.fromDto(userDto, userId));
        return UserMapping.toDto(user);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return "Пользователь успешно удален";
    }
}
