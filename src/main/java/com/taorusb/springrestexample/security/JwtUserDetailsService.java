package com.taorusb.springrestexample.security;

import com.taorusb.springrestexample.model.User;
import com.taorusb.springrestexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if(Objects.isNull(user)) {
            throw new UsernameNotFoundException("A user with " + username + " does not exist.");
        }
        return JwtUserFactory.createUser(user);
    }
}
