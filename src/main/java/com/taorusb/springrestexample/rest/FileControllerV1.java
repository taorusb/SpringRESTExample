package com.taorusb.springrestexample.rest;

import com.taorusb.springrestexample.dto.FileDto;
import com.taorusb.springrestexample.dto.UserDto;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileControllerV1 {

    private final FileService fileService;

    @Autowired
    public FileControllerV1(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/api/v1/users/{id}/files")
    public ResponseEntity<List<FileDto>> getMore(@PathVariable Long id) {
        List<FileDto> dtos = new ArrayList<>();
        try {
            fileService.getByUserId(id).forEach(file -> {
                FileDto fileDto = new FileDto();
                fileDto.setId(file.getId());
                fileDto.setName(file.getName());
                dtos.add(fileDto);
            });
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/users/{id}/files/{fileId}")
    public ResponseEntity getOne(@PathVariable Long id,@PathVariable Long fileId) {
        try {
            File file = fileService.getSingleByUserId(id, fileId);
            FileDto fileDto = new FileDto();
            fileDto.setId(file.getId());
            fileDto.setName(file.getName());
            fileDto.setURL(file.getURL());
            return ResponseEntity.ok(fileDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/file")
    public ResponseEntity addFile(@RequestBody FileDto fileDto) {
        try {
            File file = fileDto.toFile();
            fileDto = FileDto.fromFile(fileService.save(file));
            return new ResponseEntity(fileDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/v1/file")
    public ResponseEntity updateFile(@RequestBody FileDto fileDto) {
        try {
            File file = fileDto.toFile();
            fileDto = FileDto.fromFile(fileService.update(file));
            return new ResponseEntity(fileDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/api/v1/file/{id}")
    public ResponseEntity deleteFile(@PathVariable Long id) {
        try {
            File file = fileService.getById(id);
            fileService.delete(file);
            return new ResponseEntity(FileDto.fromFile(file), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}