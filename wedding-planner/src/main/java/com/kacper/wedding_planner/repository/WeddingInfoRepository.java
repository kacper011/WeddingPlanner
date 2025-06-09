package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeddingInfoRepository extends JpaRepository<WeddingInfo, Long> {
    Optional<WeddingInfo> findByUser(User user);
}
