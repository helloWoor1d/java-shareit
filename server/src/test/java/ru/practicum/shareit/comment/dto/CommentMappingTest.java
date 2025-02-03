package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentMappingTest {
    @InjectMocks
    private CommentMappingImpl commentMapping;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    private User user;
    private Item item;

    @BeforeEach
    public void init() {
        user = new User(3L, "name", "email@email.com");
        ItemRequest request = new ItemRequest(4L, "desc", user, LocalDateTime.now());
        item =  new Item(5L, "item name", "desc", true, user, request);
    }

    @Test
    public void fromDtoTest() {
        Comment comm = commentMapping.fromDto(null, null, null);
        assertThat(comm, is(nullValue()));

        comm = commentMapping.fromDto(new CommentCreateDto("text"), null, null);
        assertThat(comm, is(notNullValue()));
        assertThat(comm.getText(), is("text"));
        assertThat(comm.getAuthor(), is(nullValue()));
        assertThat(comm.getItem(), is(nullValue()));

        when(userService.getUserReference(user.getId())).thenReturn(user);
        comm = commentMapping.fromDto(new CommentCreateDto("text"), user.getId(), null);
        assertThat(comm, is(notNullValue()));
        assertThat(comm.getText(), is("text"));
        assertThat(comm.getAuthor().getId(), is(user.getId()));
        assertThat(comm.getItem(), is(nullValue()));

        when(itemService.getItemReference(item.getId())).thenReturn(item);
        comm = commentMapping.fromDto(new CommentCreateDto("text"), null, item.getId());
        assertThat(comm, is(notNullValue()));
        assertThat(comm.getText(), is("text"));
        assertThat(comm.getItem().getId(), is(item.getId()));
        assertThat(comm.getAuthor(), is(nullValue()));
    }

    @Test
    public void toDtoTest() {
        CommentGetDto commDto = commentMapping.toDto(null, null);
        assertThat(commDto, is(nullValue()));

        commDto = commentMapping.toDto(null, "meow");
        assertThat(commDto, is(notNullValue()));
        assertThat(commDto.getAuthorName(), is("meow"));
        assertThat(commDto.getText(), is(nullValue()));

        commDto = commentMapping.toDto(new Comment(1L, "text", item, user, LocalDateTime.now()), null);
        assertThat(commDto, is(notNullValue()));
        assertThat(commDto.getId(), is(1L));
        assertThat(commDto.getText(), is("text"));
        assertThat(commDto.getItemId(), is(item.getId()));
        assertThat(commDto.getAuthorName(), is(nullValue()));

        commDto = commentMapping.toDto(new Comment(1L, "text", null, user, LocalDateTime.now()), "meow");
        assertThat(commDto, is(notNullValue()));
        assertThat(commDto.getId(), is(1L));
        assertThat(commDto.getItemId(), is(nullValue()));
        assertThat(commDto.getAuthorName(), is("meow"));

        commDto = commentMapping.toDto(new Comment(1L, "text", new Item(), user, LocalDateTime.now()), "meow");
        assertThat(commDto.getId(), is(1L));
        assertThat(commDto.getItemId(), is(nullValue()));
    }
}
