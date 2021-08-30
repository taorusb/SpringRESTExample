package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.User;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    public interface PostReq {
    }

    public interface PutReq {
    }

    @NotNull(groups = UserDto.PutReq.class)
    private Long id;
    @NotNull(groups = { UserDto.PostReq.class, UserDto.PutReq.class })
    private String username;
    @NotNull(groups = UserDto.PostReq.class)
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
