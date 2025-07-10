package ru.practicum.user.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.user.model.User;

@Slf4j
@RequiredArgsConstructor
@Component
public class Client {
    @Value("${shareit-server.url}")
    private String baseUrl;
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:9090").build();

    public User getUserDetails(String email) {
        User response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/private")
                        .queryParam("email", email)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(err -> log.warn("Ошибка отправки запроса на получение user - email {}." +
                        "Error: {}", email, err.getMessage()))
                .block();
        log.debug("Запрос на сервер ресурсов для получения пользовтеля по email {}", email);
        return response;
    }
}
