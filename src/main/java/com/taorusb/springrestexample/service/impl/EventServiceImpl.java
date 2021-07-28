package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.repository.EventRepository;
import com.taorusb.springrestexample.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event getById(Long id) {
        return eventRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Event findEventByIdAndUserId(Long id, Long userId) {
        Event event = eventRepository.findEventByIdAndUserId(id, userId);
        if (Objects.isNull(event)) {
            throw new EntityNotFoundException("Entity not found.");
        }
        return event;
    }

    @Override
    public List<Event> findEventsByUserId(Long id) {
        List<Event> events = eventRepository.findEventsByUserId(id);
        if (events.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        return events;
    }
}