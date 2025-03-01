package ru.practicum.shareit.controllers.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDtoOut {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
