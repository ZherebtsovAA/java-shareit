package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        UserDto userDto = new UserDto(null, "user", "user@user.com");

        Mockito
                .when(userService.save(userDto))
                .thenAnswer(invocationOnMock -> {
                    UserDto user = invocationOnMock.getArgument(0, UserDto.class);
                    user.setId(1L);

                    return user;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void patchUpdate() throws Exception {
        UserDto userDto = new UserDto(null, "patchUser", "patchUser@user.com");
        Long userId = 1L;

        Mockito
                .when(userService.patchUpdate(userId, userDto))
                .thenAnswer(invocationOnMock -> {
                    UserDto user = invocationOnMock.getArgument(1, UserDto.class);
                    user.setId(1L);

                    return user;
                });

        mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void findById() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "user", "user@user.com");

        Mockito
                .when(userService.findById(userId))
                .thenReturn(userDto);

        mvc.perform(get("/users/" + userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void findAll() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "user", "user@user.com");

        Mockito
                .when(userService.findAll(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;

        Mockito
                .doNothing()
                .when(userService)
                .deleteById(userId);

        ResultActions response = mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void handleConstraintViolationException() throws Exception {
        UserDto userDto = new UserDto(null, "user", null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}