package ru.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@Configuration
public class SecurityConfigClient {
    @Bean
    public RegisteredClientRepository registeredClientRepository (PasswordEncoder passwordEncoder) {
        RegisteredClient client = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("ShareIt")
                .clientSecret(passwordEncoder.encode("client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://www.manning.com/authorized")
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)
                        .build())
                .build();

        RegisteredClient client2 = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("shareit-frontend")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:9090/index.html")
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(client, client2);
    }

//    @Bean
//    public ClientRegistration yandexClientRegistration() {        yandex
//        return ClientRegistration
//                .withRegistrationId("yandex")
//                .clientId("305a98649ae94831ab865805bc732e23")
//                .clientSecret("8dbea006fd77461183d39ab018d516b5")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("https://www.manning.com/authorized")
//                .scope("login:email")
//                .authorizationUri("https://oauth.yandex.ru/authorize")
//                .tokenUri("https://oauth.yandex.ru/token")
//                .userInfoUri("https://login.yandex.ru/info")
//                .userNameAttributeName("id")
//                .clientName("Yandex")
//                .build();
//    }
//
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration clientRegistration) {
//        return new InMemoryClientRegistrationRepository(clientRegistration);
//    }
}
