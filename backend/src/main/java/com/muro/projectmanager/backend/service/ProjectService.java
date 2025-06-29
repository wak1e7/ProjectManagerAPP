package com.muro.projectmanager.backend.service;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.model.Project;
import com.muro.projectmanager.backend.model.ProjectMember;
import com.muro.projectmanager.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final ProjectMemberService memberService;

    public List<ProjectDto> listByUser(Long userId) {
        return projectRepo.findAllByMembersUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto create(Long userId, CreateProjectRequest req) {
        Project p = Project.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .build();
        projectRepo.save(p);
        memberService.addCreatorAsAdmin(p.getId(), userId);
        return toDto(p);
    }

    public ProjectDto getById(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));
        return toDto(p);
    }

    @Transactional
    public ProjectDto update(Long projectId, UpdateProjectRequest req) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        return toDto(p);
    }

    @Transactional
    public void delete(Long projectId) {
        projectRepo.deleteById(projectId);
    }

    private ProjectDto toDto(Project p) {
        ProjectDto dto = new ProjectDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        return dto;
    }

    public ProjectDetailDto toDetailDto(Project p) {
        ProjectDetailDto dto = new ProjectDetailDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setMembers(
                p.getMembers().stream()
                        .map(this::memberToDto)
                        .collect(Collectors.toList())
        );
        dto.setTasks(
                p.getTasks().stream()
                        .map(this::taskToDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private ProjectMemberDto memberToDto(ProjectMember pm) {
        ProjectMemberDto m = new ProjectMemberDto();
        m.setUserId(pm.getUser().getId());
        m.setEmail(pm.getUser().getEmail());
        m.setRole(pm.getRole());
        return m;
    }

    private TaskDto taskToDto(com.muro.projectmanager.backend.model.Task t) {
        TaskDto td = new TaskDto();
        td.setId(t.getId());
        td.setTitle(t.getTitle());
        td.setDescription(t.getDescription());
        return td;
    }
}
