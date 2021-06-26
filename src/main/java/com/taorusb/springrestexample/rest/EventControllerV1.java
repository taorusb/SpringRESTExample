package com.taorusb.springrestexample.rest;

import com.taorusb.springrestexample.dto.EventDto;
import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EventControllerV1 {

    private final EventService eventService;

    @Autowired
    public EventControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/api/v1/users/{id}/events")
    public ResponseEntity<List<EventDto>> getMore(@PathVariable Long id) {
        List<EventDto> dtos = new ArrayList<>();
        try {
            eventService.findEventsByUserId(id).forEach(event -> {
                EventDto dto = new EventDto();
                dto.setId(event.getId());
                dto.setUploadDate(event.getUploadDate());
                dtos.add(dto);
            });
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/users/{id}/events/{eventId}")
    public ResponseEntity getOne(@PathVariable Long id,@PathVariable Long eventId) {
        try {
            Event event = eventService.findEventByIdAndUserId(eventId, id);
            return ResponseEntity.ok(EventDto.fromEvent(event));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
