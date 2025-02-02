package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestMappingImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = ItemRequestController.class)
@Import(ItemRequestMappingImpl.class)
public class ItemRequestControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private final ItemRequestMapping requestMapping;

    @MockBean
    private final ItemRequestService requestService;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ItemController itemController;

    private User requester;
    private Item i1;
    private ItemRequest request;

    @BeforeEach
    public void setUp() {
        User owner = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        requester = User.builder().id(2L).name("user1").email("user1@mail.ru").build();

        i1 = Item.builder().name("Item 1").description("description for item 1").available(true).owner(owner).request(request).build();
        request = ItemRequest.builder().id(1L).description("description").requester(requester).build();
    }

    @Test
    public void shouldCreateRequest() throws Exception {
        when(requestService.createRequest(any(), anyLong())).thenReturn(request);
        when(userService.getUserReference(anyLong())).thenReturn(requester);

        MockHttpServletResponse response = performCreateRequest(requester.getId(), "");
        assertThat(response.getStatus(), is(400));
        verify(requestService, Mockito.times(0)).createRequest(any(), anyLong());

        response = performCreateRequest(requester.getId(), "item");
        assertThat(response.getStatus(), is(200));
        ItemRequestGetDto responseRequest = mapper.readValue(response.getContentAsString(), ItemRequestGetDto.class);
        assertThat(responseRequest.getId(), is(request.getId()));
        verify(requestService, Mockito.times(1)).createRequest(any(), anyLong());
    }

    public MockHttpServletResponse performCreateRequest(long userId, String description) throws Exception {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder().description(description).build();
        MvcResult result = mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId).content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetUserRequests() throws Exception {
        ItemForRequestDto itemDto = ItemForRequestDto.builder().id(i1.getId()).name(i1.getDescription()).ownerId(i1.getOwner().getId()).build();
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(request));
        when(itemController.getItemsByRequestId(anyList())).thenReturn(Map.of(request.getId(), List.of(itemDto)));

        MockHttpServletResponse response = performGetUserRequests(requester.getId());
        assertThat(response.getStatus(), is(200));
        List<ItemRequestGetDto> responseItems = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});
        assertThat(responseItems.size(), is(1));
        assertThat(responseItems.getFirst().getId(), is(request.getId()));

        assertThat(responseItems.getFirst().getItems().size(), is(1));
        assertThat(responseItems.getFirst().getItems().getFirst().getId(), is(i1.getId()));
        verify(requestService, Mockito.times(1)).getUserRequests(anyLong());
    }

    public MockHttpServletResponse performGetUserRequests(long userId) throws Exception {
        MvcResult result = mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetAllRequests() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(request));

        MockHttpServletResponse response = performGetAllRequests(requester.getId(), "", "");
        assertThat(response.getStatus(), is(200));
        List<ItemRequestGetDto> responseItems = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});
        assertThat(responseItems.size(), is(1));
        assertThat(responseItems.getFirst().getId(), is(request.getId()));

        response = performGetAllRequests(requester.getId(), "0", "10");
        assertThat(response.getStatus(), is(200));
        verify(requestService, Mockito.times(2)).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    public MockHttpServletResponse performGetAllRequests(long userId, String from, String size) throws Exception {
        MvcResult result = mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId)
                        .param("from", from)
                        .param("size", size)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldGetRequest() throws Exception {
        ItemForRequestDto itemDto = ItemForRequestDto.builder().id(i1.getId()).name(i1.getDescription()).ownerId(i1.getOwner().getId()).build();
        when(requestService.getRequest(anyLong(), anyLong())).thenReturn(request);
        when(itemController.getItemsByRequestId(anyList())).thenReturn(Map.of(request.getId(), List.of(itemDto)));

        MockHttpServletResponse response = performGetRequest(requester.getId(), request.getId());
        assertThat(response.getStatus(), is(200));
        ItemRequestGetDto responseRequest = mapper.readValue(response.getContentAsString(), ItemRequestGetDto.class);
        assertThat(responseRequest.getId(), is(request.getId()));
        verify(requestService, Mockito.times(1)).getRequest(anyLong(), anyLong());
    }

    public MockHttpServletResponse performGetRequest(long userId, long requestId) throws Exception {
        MvcResult result = mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }
}
