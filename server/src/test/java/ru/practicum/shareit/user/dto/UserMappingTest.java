package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class UserMappingTest {
    private static UserMappingImpl userMapping;

    @BeforeAll
    public static void init() {
        userMapping = new UserMappingImpl();
    }

    @Test
    public void shouldReturnNullIfMappingObjectIsNull() {
        User user = userMapping.fromDto(null);
        assertThat(user, is(nullValue()));

        user = userMapping.fromDto(null, 7L);
        assertThat(user, is(nullValue()));

        UserDto userDto = userMapping.toDto(null);
        assertThat(userDto, is(nullValue()));
    }
}
