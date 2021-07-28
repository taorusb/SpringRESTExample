package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.repository.EventRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class EventServiceImplTest {

    @InjectMocks
    EventServiceImpl eventService;

    @Mock
    EventRepository eventRepository;

    Event event = new Event();

    @Before
    public void setUp() {
        event.setId(1L);
        event.setUploadDate(new Date(1L));
    }

    @Test
    public void getById_throws_exception() {
        when(eventRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> eventService.getById(anyLong()));
    }

    @Test
    public void getById_returns_event() {
        when(eventRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(event));
        assertEquals(event, eventService.getById(anyLong()));
    }

    @Test
    public void findEventByIdAndUserId_throws_exception() {
        assertThrows(EntityNotFoundException.class, () -> eventService.findEventByIdAndUserId(1L, 1L));
    }

    @Test
    public void findEventByIdAndUserId_returns_event() {
        when(eventRepository.findEventByIdAndUserId(anyLong(), anyLong())).thenReturn(event);
        assertEquals(event, eventService.findEventByIdAndUserId(1L, 1L));
    }

    @Test
    public void findEventsByUserId_throws_exception() {
        when(eventRepository.findEventsByUserId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> eventService.findEventsByUserId(1L));
    }
}