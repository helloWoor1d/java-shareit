package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @Null(groups = Create.class)
    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotNull(groups = Create.class)
    @Email(regexp = ".+[@].+[\\.].+", groups = {Create.class, Update.class})
    private String email;

public interface Create{}

public interface Update{}
}
