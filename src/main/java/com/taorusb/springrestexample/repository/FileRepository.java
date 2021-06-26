package com.taorusb.springrestexample.repository;

import com.taorusb.springrestexample.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("from File f where f.id in (select e.file.id from Event e where e.user.id = :id)")
    List<File> findByUserId(@Param("id") Long userId);

    @Query("from File f where f.id = (select e.file.id from Event e where e.user.id = :id and e.file.id = :fileId)")
    File getSingleByUserId(@Param("id") Long userId, @Param("fileId") Long fileId);
}
