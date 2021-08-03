package com.taorusb.springrestexample.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ObjectMapper.class)
public class UserRestControllerV1Test {

    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserRestControllerV1 userRestControllerV1;

    @Autowired
    ObjectMapper objectMapper;

    List<User> userList = new ArrayList<>();
    User user = new User();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRestControllerV1(userService)).build();
        user.setId(1L);
        user.setPassword("123");
        user.setUsername("username");
        userList.add(user);
        when(userService.findAll()).thenReturn(userList);
        when(userService.getById(1L)).thenReturn(user);
        when(userService.getById(2L)).thenThrow(EntityNotFoundException.class);

    }

    @WithMockUser
    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(userList.size())));

    }

    @WithMockUser
    @Test
    public void getUser_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("username")));

    }

    @WithMockUser
    @Test
    public void getUser_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void saveUser_returns_201() throws Exception {
        when(userService.save(argThat(user -> user.getId() == 1L))).thenReturn(user);
        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("username")));
    }

    @WithMockUser
    @Test
    public void saveUser_returns_400() throws Exception {
        when(userService.save(argThat(user -> user.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        user.setId(2L);
        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void updateUser_returns_202() throws Exception {
        when(userService.update(argThat(user -> user.getId() == 1L))).thenReturn(user);
        user.setId(1L);
        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("username")));
    }

    @WithMockUser
    @Test
    public void updateUser_returns_400() throws Exception {
        when(userService.update(argThat(user -> user.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        user.setId(2L);
        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void updateUser_returns_404() throws Exception {
        when(userService.update(argThat(user -> user.getId() == 3L))).thenThrow(EntityNotFoundException.class);
        user.setId(3L);
        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void deleteUser_returns_202() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{id}", 1L))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("username")));
    }

    @WithMockUser
    @Test
    public void deleteUser_returns_404() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{id}", 2L))
                .andExpect(status().isNotFound());
    }
}