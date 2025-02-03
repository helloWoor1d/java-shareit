package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMappingTest {
    @InjectMocks
    private ItemRequestMappingImpl requestMapping;
    @Mock
    private UserService userService;

    private Item item1, item2;
    private User user;

    @BeforeEach
    public void init() {
        User owner = new User(2L, "owner", "owner@email.com");
        user = new User(3L, "user", "user@email.com");
        item1 = new Item(5L, "item 1 name", "desc", true, owner, null);
        item2 = new Item(6L, "item 2 name", "desc", true, owner, null);
    }

    @Test
    public void toDtoTest() {
        ItemRequestGetDto requestDto = requestMapping.toDto(null);
        assertThat(requestDto, is(nullValue()));

        requestDto = requestMapping.toDto(null, null);
        assertThat(requestDto, is(nullValue()));

        ItemForRequestDto item1ForRequest = ItemForRequestDto.builder().id(item1.getId()).ownerId(item1.getOwner().getId()).build();
        ItemForRequestDto item2ForRequest = ItemForRequestDto.builder().id(item2.getId()).ownerId(item2.getOwner().getId()).build();
        requestDto = requestMapping.toDto(null, List.of(item1ForRequest, item2ForRequest));
        assertThat(requestDto, is(notNullValue()));
        assertThat(requestDto.getId(), is(nullValue()));
        assertThat(requestDto.items.size(), is(2));
        assertThat(requestDto.items, hasItems(item1ForRequest, item2ForRequest));

        ItemRequest request = new ItemRequest(1L, "desc", user, LocalDateTime.now());
        requestDto = requestMapping.toDto(request, null);
        assertThat(requestDto, is(notNullValue()));
        assertThat(requestDto.getId(), is(request.getId()));
        assertThat(requestDto.getItems(), is(nullValue()));
    }

    @Test
    public void fromDtoTest() {
        ItemRequest request = requestMapping.fromDto(null, user.getId());
        assertThat(request, is(nullValue()));

        when(userService.getUserReference(user.getId())).thenReturn(user);
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("description");
        request = requestMapping.fromDto(requestDto, user.getId());
        assertThat(request, is(notNullValue()));
        assertThat(request.getRequester().getId(), is(user.getId()));
    }
}
