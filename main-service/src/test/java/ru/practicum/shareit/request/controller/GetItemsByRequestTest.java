package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.dto.CommentMappingImpl;
import ru.practicum.shareit.config.TestSecurityConfig;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemMappingImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestMappingImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = {ItemRequestController.class, ItemController.class})
@Import({ItemRequestMappingImpl.class, ItemMappingImpl.class,
        CommentMappingImpl.class, TestSecurityConfig.class})
public class GetItemsByRequestTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private final ItemController itemController;

    @MockBean
    private final ItemRequestService requestService;
    @MockBean
    private final UserService userService;
    @MockBean
    private final BookingService bookingService;
    @MockBean
    private final ItemService itemService;

    @Test
    public void shouldGetItemsByRequestId() {
        ItemRequest request1 = ItemRequest.builder().id(1L).build();
        ItemRequest request2 = ItemRequest.builder().id(2L).build();
        Item item1ByRequest1 = Item.builder().id(1L).request(request1).build();
        Item item2ByRequest1 = Item.builder().id(2L).request(request1).build();
        Item item3ByRequest2 = Item.builder().id(3L).request(request2).build();

        when(itemService.getItemsByRequestId(List.of(request1.getId(), request2.getId()))).thenReturn(Map.of(
                request1.getId(), List.of(item1ByRequest1, item2ByRequest1),
                request2.getId(), List.of(item3ByRequest2)));

        Map<Long,List<ItemForRequestDto>> itemsByRequestId = itemController.getItemsByRequestId(List.of(1L, 2L));
        assertThat(itemsByRequestId.get(1L).size(), is(2));
        assertThat(itemsByRequestId.get(2L).size(), is(1));
        assertThat(itemsByRequestId.get(2L).getFirst().getId(), is(3L));
        verify(itemService, Mockito.times(1)).getItemsByRequestId(anyList());
    }
}
