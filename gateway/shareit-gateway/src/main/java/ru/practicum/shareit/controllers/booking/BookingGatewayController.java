package ru.practicum.shareit.controllers.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.controllers.booking.dto.BookingDtoIn;
import ru.practicum.shareit.controllers.booking.dto.BookingDtoOut;
import ru.practicum.shareit.controllers.item.dto.ItemDtoIn;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingGatewayController {
    private final WebClient webClient;

    public BookingGatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();

    }

    @PostMapping
    ResponseEntity<Object> addNewBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
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
    BookingDtoOut bookingConformation(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Positive @PathVariable(name = "bookingId") Long bookingId,
                                      @RequestParam(name = "approved") boolean approve) {
        log.info("PATCH/ Проверка параметров запроса метода bookingConformation, userId - {}, bookingId - {}, approve - {} ", userId, bookingId, approve);
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}")
                        .queryParam("approved",approve).build(bookingId))
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(BookingDtoOut.class)
                .block();
    }

    @GetMapping("/{bookingId}")
    BookingDtoOut checkBookingStatus(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Positive @PathVariable(name = "bookingId") Long bookingId) {
        log.info("GET/ Проверка параметров запроса метода checkBookingStatus, userId - {}, bookingId) - {}", userId, bookingId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/{bookingId}").build(bookingId))
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(BookingDtoOut.class)
                .block();
    }

    @GetMapping
    List<BookingDtoOut> findBookingsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingsByUser, userId - {},state - {}", userId, state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state",state).build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToFlux(BookingDtoOut.class)
                .collectList()
                .block();
    }

    @GetMapping("/owner")
    List<BookingDtoOut> findBookingItemsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingItemsByUser, userId - {},state - {}", userId, state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state",state).build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .bodyToFlux(BookingDtoOut.class)
                .collectList()
                .block();
    }
}
