package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    @NotNull
    private String description;

    @NotNull
    private Long requester;

    @NotNull
    private LocalDateTime created;
}
