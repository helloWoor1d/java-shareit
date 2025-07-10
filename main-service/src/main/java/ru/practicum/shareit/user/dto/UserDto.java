package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = Create.class, message = "Введите пароль!")
    @Size(groups = Create.class, min = 8, message = "Минимальное количество символов в пароле - 8")
    private String password;

public interface Create{}

public interface Update{}
}
