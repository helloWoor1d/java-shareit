package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserGetDto;
import ru.practicum.shareit.user.dto.UserMapping;
import ru.practicum.shareit.user.model.User;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserMeController {
    private final UserService userService;
    private final UserMapping userMapping;

    @GetMapping
    public ResponseEntity<UserGetDto> getCurrentUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        long userId = Long.parseLong(jwt.getSubject());
        User user = userService.getUser(userId);

        return ResponseEntity.ok().body(userMapping.toGetDto(user));
    }
}
