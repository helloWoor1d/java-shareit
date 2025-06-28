package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemWithImageDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private String imageUrl;
}
