package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.model.SecurityUser;

@RestController
@RequestMapping("/users/private")
@RequiredArgsConstructor
public class UserPrivateController {
    private final UserService userService;

    @GetMapping
    public SecurityUser getUserPrivate(@RequestParam String email) {
        return userService.getUserPrivate(email);
    }
}
