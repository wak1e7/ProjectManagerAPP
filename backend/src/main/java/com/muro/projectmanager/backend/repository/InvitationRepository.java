package com.muro.projectmanager.backend.repository;

import com.muro.projectmanager.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
}
