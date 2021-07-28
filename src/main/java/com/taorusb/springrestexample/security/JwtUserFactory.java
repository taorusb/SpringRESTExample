package com.taorusb.springrestexample.security;

import com.taorusb.springrestexample.model.Role;
import com.taorusb.springrestexample.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser createUser(User user) {
        return new JwtUser(user.getId(),
                user.getUsername(),
                user.getPassword(),
                convertToGrantedAuthorities(user.getRoles()));
    }

    private static List<GrantedAuthority> convertToGrantedAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
