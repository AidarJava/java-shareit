package ru.practicum.shareit.controllers.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.controllers.booking.dto.BookingDtoOut;
import ru.practicum.shareit.controllers.item.dto.*;
import ru.practicum.shareit.controllers.user.dto.UserDtoOut;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemGatewayController {
    private final WebClient webClient;

    public ItemGatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }

    @GetMapping
    public List<ItemDtoOut> get(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/ Проверка параметров запроса метода get, userId - {}", userId);
        return webClient.get()
                .uri("/items")
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToFlux(ItemDtoOut.class)
                .collectList()
                .block();
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchPrivate(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "text") String text) {
        log.info("GET/ Проверка параметров запроса метода searchPrivate, userId - {} text - {} ", userId, text);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items/search")
                        .queryParam("text",text).build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToFlux(ItemDtoOut.class)
                .collectList()
                .block();
    }

    @GetMapping("/{itemId}")
    public ItemDtoOutDate search(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Positive @PathVariable Long itemId) {
        log.info("GET/ Проверка параметров запроса метода search, userId - {} itemId - {} ", userId, itemId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(ItemDtoOutDate.class)
                .block();
    }

    @PostMapping
    public ItemDtoOut add(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDtoIn itemDtoIn) {
        log.info("POST/ Проверка параметров запроса метода add, userId - {} ItemDtoIn - {} ", userId, itemDtoIn);
        return webClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(itemDtoIn)
                .retrieve()
                .bodyToMono(ItemDtoOut.class)
                .block();
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                             @Positive @PathVariable(name = "itemId") Long itemId,
                             @RequestBody ItemDtoIn itemDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода update, userId - {}, itemId - {},ItemDtoId - {}", userId, itemId, itemDtoIn);
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(itemDtoIn)
                .retrieve()
                .bodyToMono(ItemDtoOut.class)
                .block();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Positive @PathVariable(name = "itemId") Long itemId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteItem, userId - {}, itemId - {} ", userId, itemId);
        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Positive @PathVariable(name = "itemId") Long itemId,
                                    @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addComment, userId - {} ItemId - {} ", userId, itemId);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}/comment").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(commentDtoIn)
                .retrieve()
                .bodyToMono(CommentDtoOut.class)
                .block();
    }
}
