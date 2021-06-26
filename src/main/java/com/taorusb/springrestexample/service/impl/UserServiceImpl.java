package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.model.Role;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.model.UserStatus;
import com.taorusb.springrestexample.repository.RoleRepository;
import com.taorusb.springrestexample.repository.UserRepository;
import com.taorusb.springrestexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,@Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        if (Objects.isNull(user.getUsername()) || Objects.isNull(user.getPassword())) {
            throw new IllegalArgumentException("Username or password can not be null");
        }
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(User entity) {
        if (Objects.isNull(entity.getId()) || Objects.isNull(entity.getUsername())) {
            throw new IllegalArgumentException("Username or id can not be null");
        }
        User user = userRepository.findById(entity.getId()).orElseThrow(EntityNotFoundException::new);
        user.setUsername(entity.getUsername());
        userRepository.save(user);
        return user;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}