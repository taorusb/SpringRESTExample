package com.taorusb.springrestexample.rest;

import com.taorusb.springrestexample.dto.UserDto;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserRestControllerV1 {

    private final UserService userService;

    @Autowired
    public UserRestControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/api/v1/users", "/api/v1/admin/users"})
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> dtos = new ArrayList<>();
        userService.findAll().forEach(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dtos.add(dto);
        });
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = {"/api/v1/users/{id}", "/api/v1/admin/users/{id}"})
    public ResponseEntity getUser(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            return ResponseEntity.ok(UserDto.getUserDto(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = {"/api/v1/user", "/api/v1/admin/user"})
    public ResponseEntity saveUser(@Validated(UserDto.PostReq.class) @RequestBody UserDto userDto) {
        User user = userDto.toUser();
        try {
            userDto = UserDto.getUserDto(userService.save(user));
            return new ResponseEntity(userDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = {"/api/v1/user", "/api/v1/admin/user"})
    public ResponseEntity updateUser(@Validated(UserDto.PutReq.class) @RequestBody UserDto userDto) {
        User user = userDto.toUser();
        try {
            userDto = UserDto.getUserDto(userService.update(user));
            return new ResponseEntity(userDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = {"/api/v1/user/{id}", "/api/v1/admin/user/{id}"})
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            userService.delete(id);
            return new ResponseEntity(UserDto.getUserDto(user), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}