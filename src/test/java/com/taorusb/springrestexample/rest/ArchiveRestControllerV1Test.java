package com.taorusb.springrestexample.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taorusb.springrestexample.model.ZipArchive;
import com.taorusb.springrestexample.service.ZipArchiveService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ObjectMapper.class)
public class ArchiveRestControllerV1Test {

    MockMvc mockMvc;

    @MockBean
    ZipArchiveService zipArchiveService;

    @MockBean
    ArchiveRestControllerV1 archiveRestControllerV1;

    @Autowired
    ObjectMapper objectMapper;

    List<ZipArchive> archiveList = new ArrayList<>();
    ZipArchive zipArchive = new ZipArchive();

    @Before
    public void setUp() throws Exception {
        zipArchive.setId(1L);
        zipArchive.setPath("/test/folder/1.txt");
        zipArchive.setUserId(1L);
        zipArchive.setName("1.txt");
        zipArchive.setLink("url");
        zipArchive.setFilePointer("1-username/");
        archiveList.add(zipArchive);
        mockMvc = MockMvcBuilders.standaloneSetup(new ArchiveRestControllerV1(zipArchiveService)).build();
        when(zipArchiveService.getByUserId(1L)).thenReturn(archiveList);
        when(zipArchiveService.getByUserId(2L)).thenThrow(EntityNotFoundException.class);
        when(zipArchiveService.getSingleByUserId(1L, 1L)).thenReturn(zipArchive);
        when(zipArchiveService.getSingleByUserId(2L, 2L)).thenThrow(EntityNotFoundException.class);
    }

    @WithMockUser
    @Test
    public void getMore_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/archives", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(archiveList.size())));
    }

    @WithMockUser
    @Test
    public void getMore_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/archives", 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getOne_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/archives/{id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void getOne_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/archives/{id}", 2L, 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void addArchive_returns_201() throws Exception {
        when(zipArchiveService.save(argThat(zipArchive -> zipArchive.getId() == 1L))).thenReturn(zipArchive);
        mockMvc.perform(post("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));

    }

    @WithMockUser
    @Test
    public void addArchive_returns_400() throws Exception {
        when(zipArchiveService.save(argThat(zipArchive -> zipArchive.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        zipArchive.setId(2L);
        mockMvc.perform(post("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void addArchive_returns_404() throws Exception {
        when(zipArchiveService.save(argThat(zipArchive -> zipArchive.getId() == 3L))).thenThrow(EntityNotFoundException.class);
        zipArchive.setId(3L);
        mockMvc.perform(post("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void updateArchive_returns_202() throws Exception {
        when(zipArchiveService.update(argThat(zipArchive -> zipArchive.getId() == 1L))).thenReturn(zipArchive);
        zipArchive.setId(1L);
        mockMvc.perform(put("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void updateArchive_returns_400() throws Exception {
        when(zipArchiveService.update(argThat(zipArchive -> zipArchive.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        zipArchive.setId(2L);
        mockMvc.perform(put("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void updateArchive_returns_404() throws Exception {
        when(zipArchiveService.update(argThat(zipArchive -> zipArchive.getId() == 3L))).thenThrow(EntityNotFoundException.class);
        zipArchive.setId(3L);
        mockMvc.perform(put("/api/v1/archive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void deleteArchive_returns_202() throws Exception {
        when(zipArchiveService.delete(1L)).thenReturn(zipArchive);
        mockMvc.perform(delete("/api/v1/archive/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zipArchive)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void deleteArchive_returns_404() throws Exception {
        when(zipArchiveService.delete(1L)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(delete("/api/v1/archive/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}