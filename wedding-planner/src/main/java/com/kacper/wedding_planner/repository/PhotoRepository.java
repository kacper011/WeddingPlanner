package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByOwnerId(Long ownerId);
}
