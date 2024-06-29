package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapping {
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto, long userId);


    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "owner", source = "userId")
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto, long userId, long itemId);
}
