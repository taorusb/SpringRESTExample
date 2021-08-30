package com.taorusb.springrestexample.repository;

import com.taorusb.springrestexample.model.BuildingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingStatusRepository extends JpaRepository<BuildingStatus, Long> {
    BuildingStatus findByName(String name);
}
