package com.taorusb.springrestexample.repository;

import com.taorusb.springrestexample.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("from File f where f.id in (select e.file.id from Event e where e.user.id = :id)")
    List<File> findByUserId(Long id);

    @Query("from File f where f.id = (select e.file.id from Event e where e.user.id = :userId and e.file.id = :id)")
    File getSingleByUserId(Long id, Long userId);
}
