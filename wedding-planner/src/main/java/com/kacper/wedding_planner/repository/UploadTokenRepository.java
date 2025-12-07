package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadTokenRepository extends JpaRepository<UploadToken, Long> {
    Optional<UploadToken> findByToken(String token);
    List<UploadToken> findByOwner(User owner);

}
