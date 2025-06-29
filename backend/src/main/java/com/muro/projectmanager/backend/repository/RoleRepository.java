package com.muro.projectmanager.backend.repository;

import com.muro.projectmanager.backend.model.Role;
import com.muro.projectmanager.backend.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
