package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.model.ZipArchive;
import com.taorusb.springrestexample.model.Event;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.repository.EventRepository;
import com.taorusb.springrestexample.repository.UserRepository;
import com.taorusb.springrestexample.repository.ArchiveRepository;
import com.taorusb.springrestexample.service.ZipArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class ZipArchiveServiceImpl implements ZipArchiveService {

    private final ArchiveRepository archiveRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AwsS3Actions s3Actions;

    @Autowired
    public ZipArchiveServiceImpl(ArchiveRepository archiveRepository,
                                 UserRepository userRepository,
                                 EventRepository eventRepository,
                                @Qualifier("s3-for-archive") AwsS3Actions s3Actions) {
        this.archiveRepository = archiveRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.s3Actions = s3Actions;
    }


    @Override
    public ZipArchive getById(Long id) {
        return archiveRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public ZipArchive update(ZipArchive entity) {
        ZipArchive zipArchive = archiveRepository.getById(entity.getId());
        s3Actions.deleteObject(zipArchive.getFilePointer() + zipArchive.getName());
        String newLink = s3Actions.addObject(entity.getFilePointer() + entity.getName(), entity.getPath());
        zipArchive.setLink(newLink);
        zipArchive.setName(entity.getName());
        zipArchive.setPath(entity.getPath());
        return archiveRepository.save(zipArchive);
    }

    @Override
    public ZipArchive save(ZipArchive entity) {
        User user = userRepository.getById(entity.getUserId());
        entity.setFilePointer(getFilePointer(user));
        entity.setLink(s3Actions.addObject(entity.getFilePointer() + entity.getName(), entity.getPath()));
        archiveRepository.save(entity);
        Event event = new Event();
        event.setUser(user);
        event.setFile(entity);
        eventRepository.save(event);
        return entity;
    }

    @Override
    public List<ZipArchive> getByUserId(Long id) {
        List<ZipArchive> zipArchives = archiveRepository.findByUserId(id);
        if (zipArchives.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        return zipArchives;
    }

    @Override
    public ZipArchive getSingleByUserId(Long id, Long userId) {
        ZipArchive zipArchive = archiveRepository.getSingleByUserId(id, userId);
        if (Objects.isNull(zipArchive)) {
            throw new EntityNotFoundException("Entity not found.");
        }
        return zipArchive;
    }

    @Override
    public ZipArchive delete(Long id) {
        ZipArchive zipArchive = getById(id);
        s3Actions.deleteObject(zipArchive.getFilePointer() + zipArchive.getName());
        archiveRepository.deleteById(id);
        return zipArchive;
    }

    private String getFilePointer(User user) {
        return user.getId() + "-" + user.getUsername() + "/";
    }
}
