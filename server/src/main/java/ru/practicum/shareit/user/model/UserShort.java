package ru.practicum.shareit.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserShort {
    Long getId();

    @JsonIgnore
    String getName();
}
