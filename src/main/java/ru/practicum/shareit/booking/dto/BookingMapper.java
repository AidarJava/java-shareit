package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public Booking mapBookingDtoInToBooking(BookingDtoIn bookingDtoIn) {
        Booking booking = new Booking();
        Item findItem = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException("Такой вещи нет в базе!"));
        booking.setId(bookingDtoIn.getId());
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        booking.setItem(findItem);
        booking.setBooker(bookingDtoIn.getBooker());
        booking.setStatus(bookingDtoIn.getStatus());
        return booking;
    }

    public BookingDtoOut mapBookingToBookingDtoOut(Booking booking) {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(booking.getId());
        bookingDtoOut.setStart(booking.getStart());
        bookingDtoOut.setEnd(booking.getEnd());
        bookingDtoOut.setItem(booking.getItem());
        bookingDtoOut.setBooker(booking.getBooker());
        bookingDtoOut.setStatus(booking.getStatus());
        return bookingDtoOut;
    }
}
