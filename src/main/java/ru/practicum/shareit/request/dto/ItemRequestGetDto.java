package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestGetDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    List<ItemForRequestDto> items;
}
