package ru.practicum.shareit.controllers.booking.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.controllers.booking.Status;
import ru.practicum.shareit.controllers.item.dto.ItemDtoOut;
import ru.practicum.shareit.controllers.user.dto.UserDtoOut;

import java.time.LocalDateTime;

@Data
public class BookingDtoOut {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    @NotNull
    ItemDtoOut item;
    @NotNull
    UserDtoOut booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
