package com.taorusb.springrestexample.service;

import com.taorusb.springrestexample.model.Event;

import java.util.List;

public interface EventService {

    Event getById(Long id);

    Event findEventByIdAndUserId(Long id, Long userId);

    List<Event> findEventsByUserId(Long id);
}
