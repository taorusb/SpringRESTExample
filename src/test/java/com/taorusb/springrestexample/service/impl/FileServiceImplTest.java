package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.repository.EventRepository;
import com.taorusb.springrestexample.repository.FileRepository;
import com.taorusb.springrestexample.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileServiceImplTest {

    @InjectMocks
    FileServiceImpl fileService;
    @Mock
    FileRepository fileRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    AwsS3Actions awsS3Actions;

    User user = new User();
    File file = new File();

    @Before
    public void setUp() {
        user.setId(1L);
        user.setUsername("username");
        file.setId(1L);
        file.setName("name");
        file.setLink("url");
        file.setPath("path");
        String filePointer = user.getId() + "-" + user.getUsername() + "/";
        file.setFilePointer(filePointer);
        file.setUserId(1L);
    }

    @Test
    public void getById_throws_exception() {
        when(fileRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> fileService.getById(anyLong()));
    }

    @Test
    public void getById_returns_file() {
        when(fileRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(file));
        assertEquals(file, fileService.getById(anyLong()));
    }

    @Test
    public void update_returns_file() {
        when(fileRepository.getById(anyLong())).thenReturn(file);
        when(awsS3Actions.addObject(anyString(), anyString())).thenReturn("url");
        when(fileRepository.save(file)).thenReturn(file);
        file = fileService.update(file);
        assertEquals(1L, file.getId());
        assertEquals("url", file.getLink());
        assertEquals("name", file.getName());
        assertEquals("path", file.getPath());
        assertEquals("1-username/", file.getFilePointer());
    }

    @Test
    public void save_returns_file() {
        when(userRepository.getById(anyLong())).thenReturn(user);
        when(awsS3Actions.addObject(anyString(), anyString())).thenReturn("url");
        when(fileRepository.save(file)).thenReturn(file);
        assertEquals(1L, fileService.save(file).getId());
        assertEquals("url", fileService.save(file).getLink());
        assertEquals("name", fileService.save(file).getName());
        assertEquals("path", fileService.save(file).getPath());
        assertEquals("1-username/", fileService.save(file).getFilePointer());
    }

    @Test
    public void getByUserId_throws_exception() {
        when(fileRepository.findByUserId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> fileService.getByUserId(1L));
    }

    @Test
    public void getByUserId_returns_collection() {
        List<File> files = new ArrayList<>();
        files.add(file);
        when(fileRepository.findByUserId(anyLong())).thenReturn(files);
        assertEquals(1, fileService.getByUserId(1L).size());
    }

    @Test
    public void getSingleByUserId_throws_exception() {
        assertThrows(EntityNotFoundException.class, () -> fileService.getSingleByUserId(1L, 1L));
    }

    @Test
    public void getSingleByUserId_returns_file() {
        when(fileRepository.getSingleByUserId(anyLong(), anyLong())).thenReturn(file);
        assertEquals(file, fileService.getSingleByUserId(1L, 1L));
    }

    @Test
    public void delete() {
        when(fileRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(file));
        assertDoesNotThrow(() -> fileService.delete(1L));
    }
}