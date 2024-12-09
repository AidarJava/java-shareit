package ru.practicum.shareit.controllers.itemRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.controllers.itemRequest.dto.ItemRequestDtoIn;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestGatewayController {
    private final WebClient webClient;

    public ItemRequestGatewayController(WebClient.Builder webClientBuilder, @Value("${shareit-server.url}") String serverUrl) {
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

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                                @RequestBody ItemRequestDtoIn req) {
        log.info("POST/ Проверка параметров запроса метода addNewRequest, ItemRequestDtoIn - {}", req);
        return webClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", id.toString())
                .bodyValue(req)
                .exchangeToMono(response -> response.toEntity(Object.class))
                .block();
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllYourselfRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAllYourselfRequests");
        return webClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", id.toString())
                .exchangeToMono(response -> response.toEntityList(Object.class))
                .block();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Object>> getAll(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAll");
        return webClient.get()
                .uri("/requests/all")
                .header("X-Sharer-User-Id", id.toString())
                .exchangeToMono(response -> response.toEntityList(Object.class))
                .block();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @PathVariable Long requestId) {
        log.info("GET/ Проверка параметров запроса метода getRequestById, requestId - {}", requestId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/requests/{requestId}").build(requestId))
                .header("X-Sharer-User-Id", id.toString())
                .exchangeToMono(response -> response.toEntity(Object.class))
                .block();
    }
}
