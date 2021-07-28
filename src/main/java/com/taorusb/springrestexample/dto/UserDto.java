package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.User;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String username;
    private String password;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        user.setPassword(password);
        return user;
    }

    public static UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        return userDto;
    }
}
