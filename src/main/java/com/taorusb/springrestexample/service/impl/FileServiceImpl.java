package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.repository.EventRepository;
import com.taorusb.springrestexample.repository.FileRepository;
import com.taorusb.springrestexample.repository.UserRepository;
import com.taorusb.springrestexample.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AwsS3Actions s3Actions;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository,
                           UserRepository userRepository,
                           EventRepository eventRepository,
                           AwsS3Actions s3Actions) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.s3Actions = s3Actions;
    }

    public File getById(Long id) {
        return fileRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public File update(File entity) {
        if (Objects.isNull(entity.getId())) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        File forUpdate = fileRepository.getById(entity.getId());
        s3Actions.deleteObject(forUpdate.getFilePointer() + forUpdate.getName());
        String newUrl = s3Actions.addObject(forUpdate.getFilePointer() + entity.getName(), entity.getPath());
        forUpdate.setURL(newUrl);
        forUpdate.setPath(entity.getPath());
        forUpdate.setName(entity.getName());
        fileRepository.save(forUpdate);
        return forUpdate;
    }

    public File save(File entity) {
        if (Objects.isNull(entity.getUserId())) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        User user = userRepository.getById(entity.getUserId());
        entity.setFilePointer(getFilePointer(user));
        entity.setURL(s3Actions.addObject(entity.getFilePointer() + entity.getName(), entity.getPath()));
        entity = fileRepository.save(entity);
        Event event = new Event();
        event.setUser(user);
        event.setFile(entity);
        eventRepository.save(event);
        return entity;
    }

    @Override
    public List<File> getByUserId(Long id) {
        List<File> files = fileRepository.findByUserId(id);
        if (files.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        return files;
    }

    @Override
    public File getSingleByUserId(Long id, Long fileId) {
        File file = fileRepository.getSingleByUserId(id, fileId);
        if (Objects.isNull(file)) {
            throw new EntityNotFoundException("Entity not found.");
        }
        return file;
    }

    @Override
    public void delete(File entity) {
        s3Actions.deleteObject(entity.getFilePointer() + entity.getName());
        fileRepository.delete(entity);
    }

    private String getFilePointer(User user) {
        return user.getId() + "-" + user.getUsername() + "/";
    }
}