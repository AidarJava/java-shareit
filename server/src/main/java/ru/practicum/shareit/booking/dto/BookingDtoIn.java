package ru.practicum.shareit.booking.dto;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingDtoIn {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
