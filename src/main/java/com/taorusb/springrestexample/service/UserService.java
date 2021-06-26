package com.taorusb.springrestexample.service;

import com.taorusb.springrestexample.model.User;

import java.util.List;

public interface UserService {

    User getById(Long id);

    void delete(Long id);

    List<User> findAll();

    User update(User entity);

    User save(User entity);

    User getByUsername(String username);
}
