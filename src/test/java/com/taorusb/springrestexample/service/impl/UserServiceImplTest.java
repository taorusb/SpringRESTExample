package com.taorusb.springrestexample.service.impl;

import com.taorusb.springrestexample.model.Role;
import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.model.UserStatus;
import com.taorusb.springrestexample.repository.RoleRepository;
import com.taorusb.springrestexample.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceImplTest {

    User user = new User();
    Role role = new Role();
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setUp() {
        List<Role> userRoles = new ArrayList<>();
        role.setName("ROLE_USER");
        userRoles.add(role);
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("123");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(userRoles);
    }

    @Test
    public void save_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> userService.save(new User()));
    }

    @Test
    public void save_returns_user() {
        User forSave = new User();
        forSave.setUsername("username");
        forSave.setPassword("123");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(bCryptPasswordEncoder.encode("123")).thenReturn("123");
        when(userRepository.save(any(User.class))).thenReturn(forSave);
        assertEquals(user, userService.save(forSave));
    }

    @Test
    public void getById_throws_exception() {
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.getById(anyLong()));
    }

    @Test
    public void getById_returns_user() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(user));
        assertEquals(user, userService.getById(anyLong()));
    }

    @Test
    public void delete() {
        assertDoesNotThrow(() -> userService.delete(anyLong()));
    }

    @Test
    public void findAll() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(1, userService.findAll().size());
    }

    @Test
    public void update_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(new User()));
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.update(user));
    }

    @Test
    public void update_returns_user() {
        User forUpdate = new User();
        forUpdate.setId(1L);
        forUpdate.setUsername("user");
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(forUpdate));
        assertEquals("user", userService.update(forUpdate).getUsername());
        assertEquals(1L, userService.update(forUpdate).getId());
    }

    @Test
    public void getByUsername_returns_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        assertEquals(user, userService.getByUsername(anyString()));
    }
}