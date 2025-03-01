package ru.practicum.shareit.controllers.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.controllers.user.dto.UserDtoIn;
import ru.practicum.shareit.controllers.user.dto.UserDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserGatewayController {
    private final WebClient webClient;

    public UserGatewayController(WebClient.Builder webClientBuilder, @Value("${shareit-server.url}") String serverUrl) {
        this.webClient = webClientBuilder.baseUrl(serverUrl)
                .filter((request, next) -> next.exchange(request)
                        .flatMap(response -> {
                            if (response.statusCode().isError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            return Mono.error(new ResponseStatusException(response.statusCode(), body));
                                        });
                            }
                            return Mono.just(response);
                        }))
                .build();
    }

    @GetMapping
    public Mono<ResponseEntity<List<UserDtoOut>>> getAllUsers() {
        log.info("GET/ Проверка запроса метода getAllUsers");
        return webClient.get()
                .uri("/users")
                .exchangeToMono(response -> response.toEntityList(UserDtoOut.class));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserDtoOut>> getUserById(@Positive @PathVariable("userId") Long userId) {
        log.info("GET/ Проверка параметров запроса метода getUserById, userId - {}", userId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                .exchangeToMono(response -> response.toEntity(UserDtoOut.class));
    }

    @PostMapping
    public Mono<ResponseEntity<UserDtoOut>> saveUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        log.info("POST/ Проверка параметров запроса метода saveUser, userDtoIn - {}", userDtoIn);
        return webClient.post()
                .uri("/users")
                .bodyValue(userDtoIn)
                .exchangeToMono(response -> response.toEntity(UserDtoOut.class));
    }

    @PatchMapping("/{userId}")
    public Mono<ResponseEntity<UserDtoOut>> updateUser(@Positive @PathVariable("userId") Long userId,
                                                       @RequestBody UserDtoIn userDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateUser, userId - {}, userDtoIn - {} ", userId, userDtoIn);
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                .bodyValue(userDtoIn)
                .exchangeToMono(response -> response.toEntity(UserDtoOut.class));
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<String>> deleteUserById(@Positive @PathVariable("userId") Long userId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteUserById, userId - {}", userId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                .exchangeToMono(response -> response.toEntity(String.class));
    }
}
