package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
   // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime start;
   // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
