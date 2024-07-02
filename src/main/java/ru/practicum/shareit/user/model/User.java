package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String email;
}
