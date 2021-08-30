package com.taorusb.springrestexample.rest;

import com.taorusb.springrestexample.dto.ZipArchiveDto;
import com.taorusb.springrestexample.model.ZipArchive;
import com.taorusb.springrestexample.service.ZipArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ArchiveRestControllerV1 {

    private final ZipArchiveService archiveService;

    @Autowired
    public ArchiveRestControllerV1(ZipArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @GetMapping(value = {"/api/v1/users/{id}/archives", "/api/v1/admin/users/{id}/archives"})
    public ResponseEntity<List<ZipArchiveDto>> getMore(@PathVariable Long id) {
        List<ZipArchiveDto> dtos = new ArrayList<>();
        try {
            archiveService.getByUserId(id).forEach(archive -> {
                ZipArchiveDto zipArchiveDto = new ZipArchiveDto();
                zipArchiveDto.setId(archive.getId());
                zipArchiveDto.setName(archive.getName());
                zipArchiveDto.setBuildStatus(archive.getBuildingStatus().getName());
                dtos.add(zipArchiveDto);
            });
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = {"/api/v1/users/{userId}/archives/{id}", "/api/v1/admin/users/{userId}/archives/{id}"})
    public ResponseEntity getOne(@PathVariable Long userId,@PathVariable Long id) {
        try {
            ZipArchive zipArchive = archiveService.getSingleByUserId(id, userId);
            ZipArchiveDto zipArchiveDto = new ZipArchiveDto();
            zipArchiveDto.setId(zipArchive.getId());
            zipArchiveDto.setName(zipArchive.getName());
            zipArchiveDto.setLink(zipArchive.getLink());
            zipArchiveDto.setBuildStatus(zipArchive.getBuildingStatus().getName());
            return ResponseEntity.ok(zipArchiveDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = {"/api/v1/archive", "/api/v1/admin/archive"})
    public ResponseEntity addArchive(@Validated(ZipArchiveDto.PostReq.class) @RequestBody ZipArchiveDto zipArchiveDto,
                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }
            ZipArchive zipArchive =  zipArchiveDto.toArchive();
            zipArchiveDto = ZipArchiveDto.fromArchive(archiveService.save(zipArchive));
            return new ResponseEntity(zipArchiveDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = {"/api/v1/archive", "/api/v1/admin/archive"})
    public ResponseEntity updateFile(@Validated(ZipArchiveDto.PutReq.class) @RequestBody ZipArchiveDto zipArchiveDto,
                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Some filed has errors.");
            }
            ZipArchive zipArchive = zipArchiveDto.toArchive();
            zipArchiveDto = ZipArchiveDto.fromArchive(archiveService.update(zipArchive));
            return new ResponseEntity(zipArchiveDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = {"/api/v1/archive/{id}", "/api/v1/admin/archive/{id}"})
    public ResponseEntity deleteArchive(@PathVariable Long id) {
        try {
            ZipArchive zipArchive = archiveService.delete(id);
            return new ResponseEntity(ZipArchiveDto.fromArchive(zipArchive), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}