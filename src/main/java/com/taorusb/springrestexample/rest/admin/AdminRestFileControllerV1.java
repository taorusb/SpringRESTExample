package com.taorusb.springrestexample.rest.admin;

import com.taorusb.springrestexample.dto.FileDto;
import com.taorusb.springrestexample.model.File;
import com.taorusb.springrestexample.service.FileService;
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
public class AdminRestFileControllerV1 {

    private final FileService fileService;

    @Autowired
    public AdminRestFileControllerV1(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/api/v1/admin/users/{id}/files")
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

    @GetMapping("/api/v1/admin/users/{userId}/files/{id}")
    public ResponseEntity getOne(@PathVariable Long userId, @PathVariable Long id) {
        try {
            File file = fileService.getSingleByUserId(id, userId);
            FileDto fileDto = new FileDto();
            fileDto.setId(file.getId());
            fileDto.setName(file.getName());
            fileDto.setLink(file.getLink());
            return ResponseEntity.ok(fileDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/admin/file")
    public ResponseEntity addFile(@Validated(FileDto.PostReq.class) @RequestBody FileDto fileDto,
                                  BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Some filed has errors.");
            }
            File file = fileDto.toFile();
            fileDto = FileDto.fromFile(fileService.save(file));
            return new ResponseEntity(fileDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/v1/admin/file")
    public ResponseEntity updateFile(@Validated(FileDto.PutReq.class) @RequestBody FileDto fileDto,
                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Some filed has errors.");
            }
            File file = fileDto.toFile();
            fileDto = FileDto.fromFile(fileService.update(file));
            return new ResponseEntity(fileDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/api/v1/admin/file/{id}")
    public ResponseEntity deleteFile(@PathVariable Long id) {
        try {
            File file = fileService.delete(id);
            return new ResponseEntity(FileDto.fromFile(file), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}