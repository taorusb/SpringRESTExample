package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.model.File;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date uploadDate;
    private Long fileId;

    public Event toEvent() {
        Event event = new Event();
        File file = new File();
        file.setId(fileId);
        event.setId(id);
        event.setUploadDate(uploadDate);
        event.setFile(file);
        return event;
    }

    public static EventDto fromEvent(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setUploadDate(event.getUploadDate());
        eventDto.setFileId(event.getFile().getId());
        return eventDto;
    }
}