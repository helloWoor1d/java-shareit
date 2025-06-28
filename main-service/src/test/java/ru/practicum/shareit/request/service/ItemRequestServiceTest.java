package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private static User userWithRequests, userWithoutRequests;
    private static ItemRequest request1, request2;

    @BeforeEach
    public void setUp() {
        userWithRequests = User.builder().name("User 1").email("user1@gmail.com").password("12345678").build();
        userWithoutRequests = User.builder().name("User 2").email("user2@gmail.com").password("12345678").build();
        userRepository.saveAll(List.of(userWithRequests, userWithoutRequests));

        request1 = new ItemRequest(null, "desc1", userWithRequests, null);
        request2 = new ItemRequest(null, "desc2", userWithRequests, null);
    }

    @Test
    public void shouldCreateItemRequest() {
        ItemRequest savedRequest = itemRequestService.createRequest(request1, request1.getRequester().getId());
        assertThat(savedRequest.getId(), notNullValue());
        assertThat(savedRequest.getCreated(), notNullValue());
        assertThat(savedRequest, is(request1));
    }

    @Test
    public void shouldGetItemRequests() {
        Exception ex = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(333L, userWithoutRequests.getId()));
        assertThat(ex.getMessage(), is("Запрос с id 333 не был найден"));

        itemRequestService.createRequest(request1, userWithRequests.getId());
        ItemRequest savedRequest = itemRequestService.getRequest(request1.getId(), userWithRequests.getId());
        assertThat(savedRequest, is(request1));

        itemRequestService.createRequest(request2, userWithRequests.getId());
        List<ItemRequest> userRequests = itemRequestService.getUserRequests(userWithRequests.getId());
        assertThat(userRequests, hasSize(2));
        assertThat(userRequests, hasItems(request1, request2));

        userRequests = itemRequestService.getUserRequests(userWithoutRequests.getId());
        assertThat(userRequests, is(empty()));

        List<ItemRequest> allRequests = itemRequestService.getAllRequests(userWithoutRequests.getId(), 0, 10);
        assertThat(allRequests, hasSize(2));
        assertThat(allRequests, hasItems(request1, request2));

        allRequests = itemRequestService.getAllRequests(userWithRequests.getId(), 0, 10);
        assertThat(allRequests, is(empty()));
    }

    @Test
    public void shouldGetRequestReference() {
        ItemRequest reference = itemRequestService.getItemRequestReference(null);
        assertThat(reference, nullValue());

        itemRequestService.createRequest(request1, request1.getRequester().getId());
        entityManager.clear();

        reference = itemRequestService.getItemRequestReference(request1.getId());
        assertThat(Hibernate.isInitialized(reference), is(false));
        assertThat(reference, hasProperty("id", is(request1.getId())));

        reference.getRequester();
        assertThat(Hibernate.isInitialized(reference), is(true));
    }
}
