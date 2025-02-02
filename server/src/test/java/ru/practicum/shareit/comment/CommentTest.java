package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.BadOperationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private static User u1, u2;
    private static Item i1, i2;

    @BeforeEach
    public void setUp() {
        u1 = User.builder()
                .name("User 1")
                .email("user1@gmail.com")
                .build();
        u2 = User.builder()
                .name("User 2")
                .email("user2@gmail.com")
                .build();
        i1 = Item.builder()
                .name("Item 1")
                .description("description for item 1")
                .available(true)
                .owner(u1)
                .build();
        i2 = Item.builder()
                .name("Item 2")
                .description("description for item 2")
                .available(true)
                .owner(u1)
                .build();
        Booking pastBooking = Booking.builder()
                .item(i1)
                .booker(u2)
                .start(LocalDateTime.now().minusDays(24))
                .end(LocalDateTime.now().minusDays(12))
                .build();
        Booking currentBooking = Booking.builder()
                .item(i2)
                .booker(u2)
                .start(LocalDateTime.now().minusDays(24))
                .end(LocalDateTime.now().plusDays(24))
                .build();
        userService.createUser(u1);
        userService.createUser(u2);
        itemService.createItem(i1);
        itemService.createItem(i2);
        bookingService.saveBooking(pastBooking);
        bookingService.saveBooking(currentBooking);
    }

    @Test
    public void shouldCreateComment() {
        Comment commentForPastBooking = Comment.builder()
                .author(u2)
                .item(i1)
                .text("comment for item 1 from user 1")
                .build();
        Comment commentForCurrentBooking = Comment.builder()
                .author(u2)
                .item(i2)
                .text("comment for item 2 from user 2")
                .build();

        Exception ex = assertThrows(BadOperationException.class,
                () -> itemService.addComment(commentForPastBooking,
                        u1.getId(),
                        commentForPastBooking.getItem().getId()));
        assertThat(ex.getMessage(), is("Комментарии могут оставлять пользователи, бравшие вещь в аренду, и только по истечении срока аренды"));

        Comment comment = itemService.addComment(commentForPastBooking,
                commentForPastBooking.getAuthor().getId(),
                commentForPastBooking.getItem().getId());
        assertThat(comment, is(commentForPastBooking));

        ex = assertThrows(BadOperationException.class,
                () -> itemService.addComment(commentForCurrentBooking,
                        commentForCurrentBooking.getAuthor().getId(),
                        commentForCurrentBooking.getItem().getId()));
        assertThat(ex.getMessage(), is("Комментарии могут оставлять пользователи, бравшие вещь в аренду, и только по истечении срока аренды"));
    }

    @Test
    public void shouldGetItemComments() {
        Comment comment1 = Comment.builder()
                .author(u2)
                .item(i1)
                .text("comment 1 for item 1 from user 2")
                .build();
        Comment comment2 = Comment.builder()
                .author(u2)
                .item(i1)
                .text("comment 2 for item 1 from user 2")
                .build();
        itemService.addComment(comment1, u2.getId(), i1.getId());
        itemService.addComment(comment2, u2.getId(), i1.getId());

        List<Comment> comments = itemService.getItemComments(List.of(i1.getId(), i2.getId()));
        assertThat(comments.size(), is(2));
        assertThat(comments, hasItems(comment1, comment2));
    }
}
