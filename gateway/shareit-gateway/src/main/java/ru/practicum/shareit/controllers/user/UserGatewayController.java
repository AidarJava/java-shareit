package ru.practicum.shareit.controllers.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.controllers.booking.dto.BookingDtoOut;
import ru.practicum.shareit.controllers.user.dto.UserDtoIn;
import ru.practicum.shareit.controllers.user.dto.UserDtoOut;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserGatewayController {
    private final WebClient webClient;

    public UserGatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }
        @GetMapping
        public List<UserDtoOut> getAllUsers () {
            log.info("GET/ Проверка запроса метода getAllUsers");
           return webClient.get()
                    .uri("/users")
                    .retrieve()
                    .bodyToFlux(UserDtoOut.class)
                    .collectList()
                    .block();
        }

        @GetMapping("/{userId}")
        public UserDtoOut getUserById (@Positive @PathVariable("userId") Long userId){
            log.info("GET/ Проверка параметров запроса метода getUserById, userId - {}", userId);
            return  webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                    .retrieve()
                    .bodyToMono(UserDtoOut.class)
                    .block();
        }

        @PostMapping
        public UserDtoOut saveUser (@Valid @RequestBody UserDtoIn userDtoIn){
            log.info("POST/ Проверка параметров запроса метода saveUser, userDtoIn - {}", userDtoIn);
            return webClient.post()
                    .uri("/users")
                    .bodyValue(userDtoIn)
                    .retrieve()
                    .bodyToMono(UserDtoOut.class)
                    .block();
        }

        @PatchMapping("/{userId}")
        public UserDtoOut updateUser (@Positive @PathVariable("userId") Long userId,
                @RequestBody UserDtoIn userDtoIn){
            log.info("PATCH/ Проверка параметров запроса метода updateUser, userId - {}, userDtoIn - {} ", userId, userDtoIn);
            return webClient.patch()
                    .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                    .bodyValue(userDtoIn)
                    .retrieve()
                    .bodyToMono(UserDtoOut.class)
                    .block();
        }

        @DeleteMapping("/{userId}")
        public ResponseEntity<String> deleteUserById (@Positive @PathVariable("userId") Long userId){
            log.info("DELETE/ Проверка параметров запроса метода deleteUserById, userId - {}", userId);
            webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/users/{userId}").build(userId))
                    .retrieve()
                    .bodyToFlux(BookingDtoOut.class)
                    .collectList()
                    .block();
        return ResponseEntity.ok("Пользователь успешно удален");
    }
}
