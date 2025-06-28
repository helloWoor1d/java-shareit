package ru.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import ru.practicum.user.security.model.SecurityUser;

import java.security.Principal;

@Configuration
public class CustomClaimsConfig {

//    @Bean
//    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
//        return (context) -> {
//            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
//                Principal principal = context.getPrincipal();
//
//                if (principal instanceof SecurityUser securityUser) {
//                    context.getClaims().claims((claims) -> {
//                        claims.put("sub", securityUser.getUserId());
//                        claims.put("role", securityUser.getAuthorities());
//                    });
//                }
//            }
//        };
//    }
}
