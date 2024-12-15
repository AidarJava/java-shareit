package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingDtoIn {
    Long id;
    @NotNull
    @FutureOrPresent
   // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime start;
    @NotNull
    @Future
   // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime end;
    @NotNull
    Long itemId;
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
