package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut addNewBooking(Long userId, BookingDtoIn booking);

    BookingDtoOut bookingConformation(Long userId, Long booking_id, boolean approved);

    BookingDtoOut checkBookingStatus(Long userId, Long booking_id);

    List<BookingDtoOut> findBookingsByUser(Long userId, String state);

    List<BookingDtoOut> findBookingItemsByUser(Long userId, String state);

}
