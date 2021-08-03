package com.taorusb.springrestexample.repository;

import com.taorusb.springrestexample.model.ZipArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchiveRepository extends JpaRepository<ZipArchive, Long> {

    @Query("from ZipArchive zip where zip.id in (select e.file.id from Event e where e.user.id = :id)")
    List<ZipArchive> findByUserId(Long id);

    @Query("from ZipArchive zip where zip.id = (select e.file.id from Event e where e.user.id = :userId and e.file.id = :id)")
    ZipArchive getSingleByUserId(Long id, Long userId);
}
