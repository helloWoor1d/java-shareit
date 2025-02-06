package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter

public class CommentGetDto {
    @NotNull
    private Long id;

    @NotBlank
    private String text;

    @NotNull
    private Long itemId;

    @NotNull
    private String authorName;

    @NotNull
    private LocalDateTime created;
}
