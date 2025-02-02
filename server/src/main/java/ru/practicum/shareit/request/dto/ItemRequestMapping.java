package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserService.class})
public interface ItemRequestMapping {
    @Mapping(target = "items", source = "responses")
    ItemRequestGetDto toDto(ItemRequest request, List<ItemForRequestDto> responses);

    @Mapping(target = "items", ignore = true)
    ItemRequestGetDto toDto(ItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "requester", source = "requesterId")
    ItemRequest fromDto(ItemRequestCreateDto dto, long requesterId);
}
