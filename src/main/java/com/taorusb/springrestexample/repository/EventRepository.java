package com.taorusb.springrestexample.repository;

import com.taorusb.springrestexample.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventByIdAndUserId(Long id, Long userId);

    List<Event> findEventsByUserId(Long id);
}
