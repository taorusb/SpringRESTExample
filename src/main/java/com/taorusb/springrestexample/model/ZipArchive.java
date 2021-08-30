package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "archives")
@Data
public class ZipArchive extends File {

    @Column(name = "project_name")
    private String projectName;

    @OneToOne
    @JoinColumn(name = "building_status", referencedColumnName = "id")
    private BuildingStatus buildingStatus;
}
