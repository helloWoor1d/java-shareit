package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.config.TestSecurityConfig;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapping;
import ru.practicum.shareit.user.dto.UserMappingImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = UserController.class)
@Import({UserMappingImpl.class, TestSecurityConfig.class})
public class UserControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private static UserMapping userMapping;

    @MockBean
    private UserService userService;

    private User u1, u2;

    @BeforeEach
    public void setUp() {
        u1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .password("1234")
                .build();
        u2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .password("1234")
                .build();
    }

    @Test
    public void shouldGetUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(u1);
        when(userService.getUser(not(eq(1L)))).thenThrow(new NotFoundException("User not found"));

        UserDto dto = UserDto.builder()
                .id(u1.getId())
                .name(u1.getName())
                .email(u1.getEmail()).build();
        MockHttpServletResponse response = performGetUser(7L);
        assertThat(response.getStatus(), is(404));
        assertThat(mapper.readValue(response.getContentAsString(), ErrorResponse.class).getError(), is("User not found"));

        response = performGetUser(1L);
        assertThat(response.getStatus(), is(200));
        assertThat(mapper.readValue(response.getContentAsString(), UserDto.class), is(dto));
    }

    private MockHttpServletResponse performGetUser(Long userId) throws Exception {
         MvcResult result = mvc.perform(get("/users/{userId}", userId)
                         .characterEncoding(StandardCharsets.UTF_8)
                         .contentType(MediaType.APPLICATION_JSON)
                         .accept(MediaType.APPLICATION_JSON))
                 .andReturn();
         return result.getResponse();
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));
        UserDto dto1 = UserDto.builder().id(u1.getId()).name(u1.getName()).email(u1.getEmail()).build();
        UserDto dto2 = UserDto.builder().id(u2.getId()).name(u2.getName()).email(u2.getEmail()).build();

        MockHttpServletResponse response = performGetAllUsers();
        assertThat(response.getStatus(), is(200));
        assertThat(mapper.readValue(response.getContentAsString(), new TypeReference<>() {}), is(List.of(dto1, dto2)));
    }

    private MockHttpServletResponse performGetAllUsers() throws Exception {
        MvcResult result = mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                 .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldCreateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name(u1.getName())
                .email(u1.getEmail()).build();
        when(userService.createUser(any())).thenReturn(u1);

        MockHttpServletResponse response = performCreateUser(dto);
        dto.setId(1L);
        assertThat(response.getStatus(), is(200));
        assertThat(mapper.readValue(response.getContentAsString(), UserDto.class), is(dto));
    }

    private MockHttpServletResponse performCreateUser(UserDto dto) throws Exception {
        MvcResult result = mvc.perform(post("/users")
                        .content("""
                             {"name":"user1",
                             "email":"user1@mail.ru",
                              "password":"12345678"}
                             """)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        u1.setName("updated name");
        when(userService.updateUser(any())).thenReturn(u1);
        UserDto dto = UserDto.builder().name("updated name").build();

        MockHttpServletResponse response = performUpdateUser(u1.getId(), dto);
        dto.setId(u1.getId());
        dto.setEmail(u1.getEmail());
        assertThat(response.getStatus(), is(200));
        assertThat(mapper.readValue(response.getContentAsString(), UserDto.class), is(dto));
    }

    private MockHttpServletResponse performUpdateUser(long userId, UserDto dto) throws Exception {
        MvcResult result = mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", u1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }
}
