package com.taorusb.springrestexample.model;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("zip")
@Data
public class ZipArchive extends File {

}
