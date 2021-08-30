package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "building_statuses")
public class BuildingStatus extends BaseEntity {

    private String name;

    @OneToOne(mappedBy = "buildingStatus")
    private ZipArchive project;
}
