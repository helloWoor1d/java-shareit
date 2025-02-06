package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentMapping;
import ru.practicum.shareit.comment.dto.CommentMappingImpl;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.mapper.ItemMappingImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = ItemController.class)
@Import({ItemMappingImpl.class, CommentMappingImpl.class, ItemMapper.class})
public class ItemControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private final BookingService bookingService;
    @MockBean
    private final ItemService itemService;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ItemRequestService requestService;

    private final ItemMapper itemMapper;
    private final CommentMapping commentMapping;

    private User owner, user;
    private Item i1;
    private Comment c1;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        user = User.builder().id(2L).name("user1").email("user1@mail.ru").build();

        i1 = Item.builder().name("Item 1").description("description for item 1").available(true).owner(owner).build();
        c1 = Comment.builder().id(1L).text("comment for item").item(i1).author(user).build();
    }

    @Test
    public void shouldGetItemForOwner() throws Exception {
        i1.setId(1L);
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(i1);
        when(itemService.getItemComments(anyList())).thenReturn(emptyList());
        when(itemService.getUserShorts(anyList())).thenReturn(null);

        MockHttpServletResponse response = performGetItem(i1.getId(), owner.getId());
        ItemGetDto itemGetDto = mapper.readValue(response.getContentAsString(), ItemGetDto.class);

        assertThat(response.getStatus(), is(200));
        assertThat(itemGetDto.getId(), is(1L));
        verify(itemService, Mockito.times(1)).getItem(1L, 1L);
        verify(bookingService, Mockito.times(1)).getLastBooking(anyList());
        verify(bookingService, Mockito.times(1)).getNextBooking(anyList());
    }

    @Test
    public void shouldGetItem() throws Exception {
        i1.setId(1L);
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(i1);
        when(itemService.getItemComments(anyList())).thenReturn(List.of(c1));
        when(itemService.getUsersByIds(anyList())).thenReturn(List.of(user));
        when(itemService.getUserShorts(anyList())).thenReturn(null);

        MockHttpServletResponse response = performGetItem(i1.getId(), user.getId());
        assertThat(response.getStatus(), is(200));
        verify(itemService, Mockito.times(1)).getItem(1L, 2L);
        verify(bookingService, Mockito.times(0)).getLastBooking(anyList());
        verify(bookingService, Mockito.times(0)).getNextBooking(anyList());
    }

    public MockHttpServletResponse performGetItem(long itemId, long userId) throws Exception {
        MvcResult result = mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetUserItems() throws Exception {
        i1.setId(1L);
        Item i2 = Item.builder().id(2L).name("Item 1").description("description for item 1").available(true).owner(owner).build();
        when(itemService.getUserItems(anyLong())).thenReturn(List.of(i1, i2));

        MockHttpServletResponse response = performGetUserItems(owner.getId());
        List<ItemGetDto> items = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(response.getStatus(), is(200));
        assertThat(items.size(), is(2));
        verify(itemService, Mockito.times(1)).getUserItems(1L);
    }

    public MockHttpServletResponse performGetUserItems(long userId) throws Exception {
        MvcResult result = mvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldCreateItem() throws Exception {
        // for fromDto mapping
        when(userService.getUserReference(anyLong())).thenReturn(owner);
        when(requestService.getItemRequestReference(anyLong())).thenReturn(null);

        when(itemService.createItem(any())).thenReturn(i1);

        ItemDto dto = ItemDto.builder().name(i1.getName()).description(i1.getDescription()).available(i1.getAvailable()).build();
        MockHttpServletResponse response = performCreateItem(dto, owner.getId());
        ItemDto responseDto = mapper.readValue(response.getContentAsString(), ItemDto.class);

        assertThat(response.getStatus(), is(200));
        assertThat(responseDto.getId(), is(i1.getId()));
        assertThat(responseDto.getRequestId(), is(nullValue()));
        verify(itemService, Mockito.times(1)).createItem(any());
    }

    public MockHttpServletResponse performCreateItem(ItemDto dto, long userId) throws Exception {
        MvcResult result = mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        i1.setId(1L);
        i1.setName("updated name");
        i1.setDescription("updated description");
        when(itemService.updateItem(any())).thenReturn(i1);
        ItemDto dto = ItemDto.builder().name("updated name").description("updated description").build();

        MockHttpServletResponse response = performUpdateItem(i1.getId(), owner.getId(), dto);
        ItemDto responseDto = mapper.readValue(response.getContentAsString(), ItemDto.class);

        assertThat(response.getStatus(), is(200));
        assertThat(responseDto.getId(), is(i1.getId()));
        assertThat(responseDto.getName(), is(i1.getName()));
        assertThat(responseDto.getDescription(), is(i1.getDescription()));
        verify(itemService, Mockito.times(1)).updateItem(any());
    }

    public MockHttpServletResponse performUpdateItem(long itemId, long userId, ItemDto dto) throws Exception {
        MvcResult result = mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldDeleteItem() throws Exception {
        i1.setId(1L);
        mvc.perform(delete("/items/{itemId}", i1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        verify(itemService, Mockito.times(1)).deleteItem(i1.getId());
    }

    @Test
    public void shouldSearchItem() throws Exception {
        when(itemService.searchItem(anyString(), anyLong())).thenReturn(List.of(i1));
        MockHttpServletResponse response = performSearchItem("Item 1", owner.getId());

        assertThat(response.getStatus(), is(200));
        verify(itemService, Mockito.times(1)).searchItem("Item 1", 1L);
    }

    public MockHttpServletResponse performSearchItem(String query, long userId) throws Exception {
        MvcResult result = mvc.perform(get("/items/search")
                        .param("text", query)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldAddComment() throws Exception {
        i1.setId(1L);
        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(c1);
        when(itemService.getItemReference(anyLong())).thenReturn(i1);
        when(userService.getUserReference(anyLong())).thenReturn(owner);
        when(itemService.getUsersByIds(anyList())).thenReturn(List.of(user));

        CommentCreateDto  dto = new CommentCreateDto("comment for item");
        MockHttpServletResponse response = performAddComment(i1.getId(), owner.getId(), dto);
        assertThat(response.getStatus(), is(200));
        verify(itemService, Mockito.times(1)).addComment(any(), anyLong(), anyLong());
    }

    public MockHttpServletResponse performAddComment(long itemId, long userId, CommentCreateDto commentDto) throws Exception {
        MvcResult result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }
}
