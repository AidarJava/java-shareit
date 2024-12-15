package ru.practicum.shareit.booking.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingDtoOut {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
