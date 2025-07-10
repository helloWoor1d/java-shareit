package ru.practicum.user.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.practicum.user.client.Client;
import ru.practicum.user.model.User;
import ru.practicum.user.security.model.SecurityUser;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientUserDetailsService implements UserDetailsService {
    private final Client client;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = client.getUserDetails(email);
        log.debug("Получены данные пользователя от сервиса ресурсов");
        return new SecurityUser(user);
    }
}
