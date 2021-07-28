package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Data
public class File extends BaseEntity {

    private String name;

    @Transient
    private Long userId;

    @Transient
    private String path;

    @Column(name = "file_pointer")
    private String filePointer;

    @Column(name = "url")
    private String URL;

}
