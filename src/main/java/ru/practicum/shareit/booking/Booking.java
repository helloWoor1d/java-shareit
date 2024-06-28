package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long booker;

    private Status status;
}
