package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.user.model.UserShort;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter

public class BookingGetDto {
    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Status status;

    @NotNull
    private UserShort booker;

    @NotNull
    private ItemShort item;
}
