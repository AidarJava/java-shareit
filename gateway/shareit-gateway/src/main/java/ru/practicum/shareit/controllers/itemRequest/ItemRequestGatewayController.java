package ru.practicum.shareit.controllers.itemRequest;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.controllers.item.dto.ItemDtoIn;
import ru.practicum.shareit.controllers.itemRequest.dto.ItemRequestDtoIn;
import ru.practicum.shareit.controllers.itemRequest.dto.ItemRequestDtoOut;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestGatewayController {
    private final WebClient webClient;

    public ItemRequestGatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }

    @PostMapping
    public ItemRequestDtoOut addNewRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                           @RequestBody ItemRequestDtoIn req) {
        log.info("POST/ Проверка параметров запроса метода addNewRequest, ItemRequestDtoIn - {}", req);
        return webClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", id.toString())
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ItemRequestDtoOut.class)
                .block();
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAllYourselfRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAllYourselfRequests");
        return webClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", id.toString())
                .retrieve()
                .bodyToFlux(ItemRequestDtoOut.class)
                .collectList()
                .block();
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAll");
        return webClient.get()
                .uri("/requests/all")
                .header("X-Sharer-User-Id", id.toString())
                .retrieve()
                .bodyToFlux(ItemRequestDtoOut.class)
                .collectList()
                .block();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                            @PathVariable Long requestId) {
        log.info("GET/ Проверка параметров запроса метода getRequestById, requestId - {}", requestId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/requests/{requestId}").build(requestId))
                .header("X-Sharer-User-Id", id.toString())
                .retrieve()
                .bodyToMono(ItemRequestDtoOut.class)
                .block();
    }
}
