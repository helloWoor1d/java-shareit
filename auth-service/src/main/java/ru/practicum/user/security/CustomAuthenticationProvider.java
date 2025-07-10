package ru.practicum.user.security;

import log.model.SecurityLogDto;
import log.model.enums.LogLevel;
import log.model.enums.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.practicum.log.LogService;
import ru.practicum.user.security.model.SecurityUser;

import java.time.LocalDateTime;

import static log.model.enums.LogEvent.LOGIN_FAILED;
import static log.model.enums.LogEvent.LOGIN_SUCCESS;


@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final String AUTH_METHOD = "email/password";

    @Value("${server.name}")
    private String serverName;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final LogService logService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);

        SecurityLogDto log = SecurityLogDto.builder()
                .authMethod(AUTH_METHOD)
                .service(serverName)
                .level(LogLevel.INFO)
                .type(LogType.AUTH)
                .userId(securityUser.getUserId())
                .timestamp(LocalDateTime.now())
                .build();

        if (passwordEncoder.matches(password, securityUser.getPassword())) {
            log.setEvent(LOGIN_SUCCESS);
            logService.send(log);

            return new UsernamePasswordAuthenticationToken(
                   securityUser, password, securityUser.getAuthorities());
        } else {
            log.setEvent(LOGIN_FAILED);
            logService.send(log);
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType
                .equals(UsernamePasswordAuthenticationToken.class);
    }
}
