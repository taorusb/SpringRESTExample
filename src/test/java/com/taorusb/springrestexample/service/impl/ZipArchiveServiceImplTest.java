package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.model.ZipArchive;
import com.taorusb.springrestexample.repository.ArchiveRepository;
import com.taorusb.springrestexample.repository.EventRepository;
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
public class ZipArchiveServiceImplTest {

    @InjectMocks
    ZipArchiveServiceImpl zipArchiveService;

    @Mock
    ArchiveRepository archiveRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AwsS3Actions awsS3Actions;

    User user = new User();
    ZipArchive zipArchive = new ZipArchive();

    @Before
    public void setUp() throws Exception {
        user.setId(1L);
        user.setUsername("username");
        zipArchive.setId(1L);
        zipArchive.setName("name");
        zipArchive.setLink("url");
        zipArchive.setPath("path");
        String filePointer = user.getId() + "-" + user.getUsername() + "/";
        zipArchive.setFilePointer(filePointer);
        zipArchive.setUserId(1L);

    }
    @Test
    public void getById_throws_exception() {
        when(archiveRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> zipArchiveService.getById(anyLong()));
    }

    @Test
    public void getById_returns_zipArchive() {
        when(archiveRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(zipArchive));
        assertEquals(zipArchive, zipArchiveService.getById(anyLong()));
    }

    @Test
    public void update_returns_zipArchive() {
        when(archiveRepository.getById(anyLong())).thenReturn(zipArchive);
        when(awsS3Actions.addObject(anyString(), anyString())).thenReturn("url");
        when(archiveRepository.save(zipArchive)).thenReturn(zipArchive);
        zipArchive = zipArchiveService.update(zipArchive);
        assertEquals(1L, zipArchive.getId());
        assertEquals("url", zipArchive.getLink());
        assertEquals("name", zipArchive.getName());
        assertEquals("path", zipArchive.getPath());
        assertEquals("1-username/", zipArchive.getFilePointer());
    }

    @Test
    public void save_returns_zipArchive() {
        when(userRepository.getById(anyLong())).thenReturn(user);
        when(awsS3Actions.addObject(anyString(), anyString())).thenReturn("url");
        when(archiveRepository.save(zipArchive)).thenReturn(zipArchive);
        assertEquals(1L, zipArchiveService.save(zipArchive).getId());
        assertEquals("url", zipArchiveService.save(zipArchive).getLink());
        assertEquals("name", zipArchiveService.save(zipArchive).getName());
        assertEquals("path", zipArchiveService.save(zipArchive).getPath());
        assertEquals("1-username/", zipArchiveService.save(zipArchive).getFilePointer());
    }

    @Test
    public void getByUserId_throws_exception() {
        when(archiveRepository.findByUserId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> zipArchiveService.getByUserId(1L));
    }

    @Test
    public void getByUserId_returns_collection() {
        List<ZipArchive> archives = new ArrayList<>();
        archives.add(zipArchive);
        when(archiveRepository.findByUserId(anyLong())).thenReturn(archives);
        assertEquals(1, zipArchiveService.getByUserId(1L).size());
    }

    @Test
    public void getSingleByUserId_throws_exception() {
        assertThrows(EntityNotFoundException.class, () -> zipArchiveService.getSingleByUserId(1L, 1L));
    }

    @Test
    public void getSingleByUserId_returns_zipArchive() {
        when(archiveRepository.getSingleByUserId(anyLong(), anyLong())).thenReturn(zipArchive);
        assertEquals(zipArchive, zipArchiveService.getSingleByUserId(1L, 1L));
    }

    @Test
    public void delete() {
        when(archiveRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(zipArchive));
        assertDoesNotThrow(() -> zipArchiveService.delete(1L));
    }
}