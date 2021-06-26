package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity {

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany
    @JoinTable(name = "events",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "file_id", referencedColumnName = "id")})
    private List<File> files;

    @OneToMany(mappedBy = "user")
    private List<Event> events;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

}
