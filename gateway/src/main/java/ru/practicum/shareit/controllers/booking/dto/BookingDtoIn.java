package ru.practicum.shareit.controllers.booking.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.controllers.booking.Status;
import ru.practicum.shareit.controllers.user.dto.UserDtoIn;

import java.time.LocalDateTime;

@Data
public class BookingDtoIn {
    Long id;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    @NotNull
    Long itemId;
    UserDtoIn booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
