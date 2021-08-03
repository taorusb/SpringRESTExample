package com.taorusb.springrestexample.service;

import com.taorusb.springrestexample.model.File;

import java.util.List;

public interface FileService {

    File getById(Long id);

    File update(File entity);

    File save(File entity);

    List<File> getByUserId(Long id);

    File getSingleByUserId(Long id, Long userId);

    File delete(Long id);
}
