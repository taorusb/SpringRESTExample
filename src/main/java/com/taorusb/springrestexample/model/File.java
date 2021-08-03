package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "file_type")
@Data
public class File extends BaseEntity {

    private String name;

    @Transient
    private Long userId;

    @Transient
    private String path;

    @Column(name = "file_pointer")
    private String filePointer;

    @Column(name = "link")
    private String link;

}
