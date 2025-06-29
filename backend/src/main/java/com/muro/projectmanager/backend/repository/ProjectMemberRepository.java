package com.muro.projectmanager.backend.repository;

import com.muro.projectmanager.backend.model.ProjectMember;
import com.muro.projectmanager.backend.model.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findAllByProjectId(Long projectId);
}
