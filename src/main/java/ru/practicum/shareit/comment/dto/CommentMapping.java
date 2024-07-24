package ru.practicum.shareit.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapping {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "authorId", source = "userId")
    @Mapping(target = "created", ignore = true)
    Comment fromDto(CommentCreateDto dto, Long userId, Long itemId);

    @Mapping(target = "authorName", source = "authorName")
    CommentGetDto toDto(Comment comment, String authorName);
}
