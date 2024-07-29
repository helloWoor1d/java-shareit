package ru.practicum.shareit.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@Mapper(componentModel = "spring", uses = {ItemService.class, UserService.class})
public interface CommentMapping {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "itemId")
    @Mapping(target = "author", source = "userId")
    @Mapping(target = "created", ignore = true)
    Comment fromDto(CommentCreateDto dto, Long userId, Long itemId);

    @Mapping(target = "authorName", source = "authorName")
    @Mapping(target = "itemId", source = "comment.item.id")
    CommentGetDto toDto(Comment comment, String authorName);
}
