package ru.practicum.shareit.controllers.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.controllers.item.dto.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemGatewayController {
    private final WebClient webClient;

    public ItemGatewayController(WebClient.Builder webClientBuilder, @Value("${shareit-server.url}") String serverUrl) {
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
    public Mono<ResponseEntity<List<ItemDtoOut>>> get(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/ Проверка параметров запроса метода get, userId - {}", userId);
        return webClient.get()
                .uri("/items")
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntityList(ItemDtoOut.class));
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<ItemDtoOut>>> searchPrivate(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @RequestParam(name = "text") String text) {
        log.info("GET/ Проверка параметров запроса метода searchPrivate, userId - {} text - {} ", userId, text);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items/search")
                        .queryParam("text", text).build())
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntityList(ItemDtoOut.class));
    }

    @GetMapping("/{itemId}")
    public Mono<ResponseEntity<ItemDtoOutDate>> search(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @Positive @PathVariable Long itemId) {
        log.info("GET/ Проверка параметров запроса метода search, userId - {} itemId - {} ", userId, itemId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntity(ItemDtoOutDate.class));
    }

    @PostMapping
    public Mono<ResponseEntity<ItemDtoOut>> add(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemDtoIn itemDtoIn) {
        log.info("POST/ Проверка параметров запроса метода add, userId - {} ItemDtoIn - {} ", userId, itemDtoIn);
        return webClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(itemDtoIn)
                .exchangeToMono(response -> response.toEntity(ItemDtoOut.class));
    }

    @PatchMapping("/{itemId}")
    public Mono<ResponseEntity<ItemDtoOut>> update(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @Positive @PathVariable(name = "itemId") Long itemId,
                                                   @RequestBody ItemDtoIn itemDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода update, userId - {}, itemId - {},ItemDtoId - {}", userId, itemId, itemDtoIn);
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(itemDtoIn)
                .exchangeToMono(response -> response.toEntity(ItemDtoOut.class));
    }

    @DeleteMapping("/{itemId}")
    public Mono<ResponseEntity<Void>> deleteItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Positive @PathVariable(name = "itemId") Long itemId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteItem, userId - {}, itemId - {} ", userId, itemId);
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntity(Void.class));
    }

    @PostMapping("/{itemId}/comment")
    public Mono<ResponseEntity<CommentDtoOut>> addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @Positive @PathVariable(name = "itemId") Long itemId,
                                                          @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addComment, userId - {} ItemId - {} ", userId, itemId);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/{itemId}/comment").build(itemId))
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(commentDtoIn)
                .exchangeToMono(response -> response.toEntity(CommentDtoOut.class));
    }
}
