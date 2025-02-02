package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    @Null(groups = {Create.class})
    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotNull(groups = Create.class)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private Long requestId;

public interface Create {}
}



