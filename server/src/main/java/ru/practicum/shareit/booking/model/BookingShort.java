package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface BookingShort {
    Long getId();

    @JsonProperty("bookerId")
    Long getBookerId();

    @JsonIgnore
    Long getItemId();
}
