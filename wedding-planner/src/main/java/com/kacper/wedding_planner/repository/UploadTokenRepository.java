package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.UploadToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadTokenRepository extends JpaRepository<UploadToken, Long> {
    Optional<UploadToken> findByToken(String token);
}
