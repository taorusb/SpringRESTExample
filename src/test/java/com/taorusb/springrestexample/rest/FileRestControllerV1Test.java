package com.taorusb.springrestexample.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.service.FileService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ObjectMapper.class)
public class FileRestControllerV1Test {

    MockMvc mockMvc;

    @MockBean
    FileService fileService;

    @MockBean
    FileRestControllerV1 fileRestControllerV1;

    @Autowired
    ObjectMapper objectMapper;

    List<File> fileList = new ArrayList<>();
    File file = new File();

    @Before
    public void setUp() throws Exception {
        file.setId(1L);
        file.setPath("/test/folder/1.txt");
        file.setUserId(1L);
        file.setName("1.txt");
        file.setLink("url");
        file.setFilePointer("1-username/");
        fileList.add(file);
        mockMvc = MockMvcBuilders.standaloneSetup(new FileRestControllerV1(fileService)).build();
        when(fileService.getByUserId(1L)).thenReturn(fileList);
        when(fileService.getByUserId(2L)).thenThrow(EntityNotFoundException.class);
        when(fileService.getSingleByUserId(1L, 1L)).thenReturn(file);
        when(fileService.getSingleByUserId(2L, 2L)).thenThrow(EntityNotFoundException.class);
    }

    @WithMockUser
    @Test
    public void getMore_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(fileList.size())));
    }

    @WithMockUser
    @Test
    public void getMore_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/files", 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getOne_returns_200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/files/{id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void getOne_returns_404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/files/{fileId}", 2L, 2L))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void addFile_returns_201() throws Exception {
        when(fileService.save(argThat(file -> file.getId() == 1L))).thenReturn(file);
        mockMvc.perform(post("/api/v1/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));

    }

    @WithMockUser
    @Test
    public void addFile_returns_400() throws Exception {
        when(fileService.save(argThat(file -> file.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        file.setId(2L);
        mockMvc.perform(post("/api/v1/file")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(file)))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void addFile_returns_404() throws Exception {
        when(fileService.save(argThat(file -> file.getId() == 3L))).thenThrow(EntityNotFoundException.class);
        file.setId(3L);
        mockMvc.perform(post("/api/v1/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void updateFile_returns_202() throws Exception {
        when(fileService.update(argThat(file -> file.getId() == 1L))).thenReturn(file);
        file.setId(1L);
        mockMvc.perform(put("/api/v1/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void updateFile_returns_400() throws Exception {
        when(fileService.update(argThat(file -> file.getId() == 2L))).thenThrow(IllegalArgumentException.class);
        file.setId(2L);
        mockMvc.perform(put("/api/v1/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void updateFile_returns_404() throws Exception {
        when(fileService.update(argThat(file -> file.getId() == 3L))).thenThrow(EntityNotFoundException.class);
        file.setId(3L);
        mockMvc.perform(put("/api/v1/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void deleteFile_returns_202() throws Exception {
        when(fileService.delete(1L)).thenReturn(file);
        mockMvc.perform(delete("/api/v1/file/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("1.txt")))
                .andExpect(jsonPath("$.link", is("url")));
    }

    @WithMockUser
    @Test
    public void deleteFile_returns_404() throws Exception {
        when(fileService.delete(1L)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(delete("/api/v1/file/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}