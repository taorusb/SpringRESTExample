package com.taorusb.springrestexample.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ObjectMapper.class)
public class EventRestControllerV1Test {

    MockMvc mockMvc;

    @MockBean
    EventService eventService;

    @MockBean
    EventRestControllerV1 eventRestControllerV1;

    List<Event> eventList = new ArrayList<>();
    Event event = new Event();
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Before
    public void setUp() throws Exception {
        File file = new File();
        file.setId(1L);
        event.setId(1L);
        event.setUploadDate(new Date());
        event.setFile(file);
        eventList.add(event);
        mockMvc = MockMvcBuilders.standaloneSetup(new EventRestControllerV1(eventService)).build();
        when(eventService.findEventsByUserId(1L)).thenReturn(eventList);
        when(eventService.findEventsByUserId(2L)).thenThrow(EntityNotFoundException.class);
        when(eventService.findEventByIdAndUserId(1L, 1L)).thenReturn(event);
        when(eventService.findEventByIdAndUserId(2L, 2L)).thenThrow(EntityNotFoundException.class);
    }

    @WithMockUser
    @Test
    public void getMore_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/events", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(eventList.size())));
    }

    @WithMockUser
    @Test
    public void getMore_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/events", 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getOne_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/events/{id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.uploadDate", is(simpleDateFormat.format(new Date()))))
                .andExpect(jsonPath("$.fileId", is(1)));
    }

    @WithMockUser
    @Test
    public void getOne_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/events/{eventId}", 2L, 2L))
                .andExpect(status().isNotFound());
    }
}