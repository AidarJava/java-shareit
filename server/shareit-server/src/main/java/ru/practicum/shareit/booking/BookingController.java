package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut addNewBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addNewBooking, userId - {}", userId);
        return bookingService.addNewBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    BookingDtoOut bookingConformation(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Positive @PathVariable(name = "bookingId") Long bookingId,
                                      @RequestParam(name = "approved") boolean approve) {
        log.info("PATCH/ Проверка параметров запроса метода bookingConformation, userId - {}, bookingId - {}, approve - {} ", userId, bookingId, approve);
        return bookingService.bookingConformation(userId, bookingId, approve);
    }

    @GetMapping("/{bookingId}")
    BookingDtoOut checkBookingStatus(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Positive @PathVariable(name = "bookingId") Long bookingId) {
        log.info("GET/ Проверка параметров запроса метода checkBookingStatus, userId - {}, bookingId) - {}", userId, bookingId);
        return bookingService.checkBookingStatus(userId, bookingId);
    }

    @GetMapping
    List<BookingDtoOut> findBookingsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingsByUser, userId - {},state - {}", userId, state);
        return bookingService.findBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    List<BookingDtoOut> findBookingItemsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("POST/ Проверка параметров запроса метода findBookingItemsByUser, userId - {},state - {}", userId, state);
        return bookingService.findBookingItemsByUser(userId, state);
    }
}
