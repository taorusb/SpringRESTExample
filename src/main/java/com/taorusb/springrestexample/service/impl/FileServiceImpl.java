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
import org.springframework.beans.factory.annotation.Qualifier;
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
                          @Qualifier("s3-for-file") AwsS3Actions s3Actions) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.s3Actions = s3Actions;
    }

    public File getById(Long id) {
        return fileRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public File update(File entity) {
        File file = fileRepository.getById(entity.getId());
        s3Actions.deleteObject(file.getFilePointer() + file.getName());
        String newUrl = s3Actions.addObject(file.getFilePointer() + entity.getName(), entity.getPath());
        file.setLink(newUrl);
        file.setPath(entity.getPath());
        file.setName(entity.getName());
        return fileRepository.save(file);
    }

    public File save(File entity) {
        User user = userRepository.getById(entity.getUserId());
        entity.setFilePointer(getFilePointer(user));
        entity.setLink(s3Actions.addObject(entity.getFilePointer() + entity.getName(), entity.getPath()));
        fileRepository.save(entity);
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
    public File getSingleByUserId(Long id, Long userId) {
        File file = fileRepository.getSingleByUserId(id, userId);
        if (Objects.isNull(file)) {
            throw new EntityNotFoundException("Entity not found.");
        }
        return file;
    }

    @Override
    public File delete(Long id) {
        File file = getById(id);
        s3Actions.deleteObject(file.getFilePointer() + file.getName());
        fileRepository.deleteById(id);
        return file;
    }

    private String getFilePointer(User user) {
        return user.getId() + "-" + user.getUsername() + "/";
    }
}