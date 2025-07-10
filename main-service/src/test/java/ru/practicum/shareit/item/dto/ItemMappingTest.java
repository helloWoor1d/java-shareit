package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
public class ItemMappingTest {
    @InjectMocks
    private ItemMappingImpl itemMapping;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService requestService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(3L)
                .name("name")
                .email("email@email.com")
                .build();
    }

    @Test
    public void toDtoTest() {
        ItemDto dto = itemMapping.toDto(null);
        assertThat(dto, is(nullValue()));

        Item item = Item.builder()
                .id(5L)
                .name("item name")
                .description("desc")
                .available(true)
                .owner(user)
                .request(new ItemRequest())
                .build();
        dto = itemMapping.toDto(item);
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(5L));
        assertThat(dto.getRequestId(), is(nullValue()));

        item = Item.builder()
                .id(5L)
                .name("item name")
                .description("desc")
                .available(true)
                .owner(user)
                .request(new ItemRequest(1L, "desc", user, LocalDateTime.now()))
                .build();
        dto = itemMapping.toDto(item);
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(5L));
        assertThat(dto.getRequestId(), is(1L));
    }

    @Test
    public void fromDtoTest() {
        Item item = itemMapping.fromDto(null, user.getId());
        assertThat(item, is(nullValue()));

        item = itemMapping.fromDto(null, user.getId(), 7L);
        assertThat(item, is(nullValue()));
    }

    @Test
    public void toGetDtoTest() {
        ItemGetDto dto = itemMapping.toGetDto(null, null, null, null);
        assertThat(dto, is(nullValue()));

        CommentGetDto c1 = CommentGetDto.builder().id(7L).build();
        CommentGetDto c2 = CommentGetDto.builder().id(8L).build();
        dto = itemMapping.toGetDto(null, null, null, List.of(c1, c2));
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(nullValue()));
        assertThat(dto.getComments().size(), is(2));
        assertThat(dto.getComments(), hasItems(c1, c2));
    }

    @Test
    public void toRequestDtoTest() {
        ItemForRequestDto dto = itemMapping.toRequestDto(null);
        assertThat(dto, is(nullValue()));

        Item item = Item.builder()
                .id(5L)
                .name("item name")
                .description("desc")
                .available(true)
                .owner(null)
                .request(new ItemRequest())
                .build();
        dto = itemMapping.toRequestDto(item);
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getId(), is(item.getId()));
        assertThat(dto.getOwnerId(), is(nullValue()));

        item = Item.builder()
                .id(5L)
                .name("item name")
                .description("desc")
                .available(true)
                .owner(new User())
                .request(new ItemRequest())
                .build();
        dto = itemMapping.toRequestDto(item);
        assertThat(dto, is(notNullValue()));
        assertThat(dto.getOwnerId(), is(nullValue()));
    }
}
