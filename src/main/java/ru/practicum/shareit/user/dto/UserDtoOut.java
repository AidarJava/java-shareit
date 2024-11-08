package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDtoOut {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
