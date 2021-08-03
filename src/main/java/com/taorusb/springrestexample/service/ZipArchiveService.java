package com.taorusb.springrestexample.service;

import com.taorusb.springrestexample.model.ZipArchive;

import java.util.List;

public interface ZipArchiveService {

    ZipArchive getById(Long id);

    ZipArchive update(ZipArchive entity);

    ZipArchive save(ZipArchive entity);

    List<ZipArchive> getByUserId(Long id);

    ZipArchive getSingleByUserId(Long id, Long userId);

    ZipArchive delete(Long id);
}
