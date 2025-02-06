package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ItemShort {
    Long getId();

    String getName();

    @JsonIgnore
    Long getOwnerId();
}
