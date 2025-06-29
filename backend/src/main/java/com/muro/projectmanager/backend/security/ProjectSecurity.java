package com.muro.projectmanager.backend.security;

import com.muro.projectmanager.backend.model.ProjectMember;
import com.muro.projectmanager.backend.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {
    private final ProjectMemberRepository pmRepo;

    public boolean canAccess(UserPrincipal user, Long projectId) {
        return pmRepo.findById(new com.muro.projectmanager.backend.model.ProjectMemberId(projectId, user.getId()))
                .isPresent();
    }

    public boolean isAdmin(UserPrincipal user, Long projectId) {
        Optional<ProjectMember> pm = pmRepo.findById(
                new com.muro.projectmanager.backend.model.ProjectMemberId(projectId, user.getId())
        );
        return pm.map(m -> m.getRole() == com.muro.projectmanager.backend.model.ProjectRoleName.PROJECT_ADMIN)
                .orElse(false);
    }
}
