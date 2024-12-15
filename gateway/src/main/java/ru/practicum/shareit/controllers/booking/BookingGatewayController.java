package ru.practicum.shareit.controllers.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.controllers.booking.dto.BookingDtoIn;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingGatewayController {
    private final WebClient webClient;

    public BookingGatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090")
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
    public ResponseEntity<Object> addNewBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addNewBooking, userId - {}", userId);
        return webClient.post()
                .uri("/bookings")
                .header("X-Sharer-User-Id", userId.toString())
                .bodyValue(bookingDtoIn)
                .exchangeToMono(response -> response.toEntity(Object.class))
                .block();
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> bookingConformation(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @Positive @PathVariable(name = "bookingId") Long bookingId,
                                               @RequestParam(name = "approved") boolean approve) {
        log.info("PATCH/ Проверка параметров запроса метода bookingConformation, userId - {}, bookingId - {}, approve - {} ", userId, bookingId, approve);
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .queryParam("approved", approve).build(bookingId))
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntity(Object.class))
                .block();
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> checkBookingStatus(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable(name = "bookingId") Long bookingId) {
        log.info("GET/ Проверка параметров запроса метода checkBookingStatus, userId - {}, bookingId) - {}", userId, bookingId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}").build(bookingId))
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntity(Object.class))
                .block();
    }

    @GetMapping
    ResponseEntity<List<Object>> findBookingsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingsByUser, userId - {},state - {}", userId, state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", state).build())
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntityList(Object.class))
                .block();
    }

    @GetMapping("/owner")
    ResponseEntity<List<Object>> findBookingItemsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingItemsByUser, userId - {},state - {}", userId, state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", state).build())
                .header("X-Sharer-User-Id", userId.toString())
                .exchangeToMono(response -> response.toEntityList(Object.class))
                .block();
    }
}