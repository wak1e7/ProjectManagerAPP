package com.muro.projectmanager.backend.controller;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.security.UserPrincipal;
import com.muro.projectmanager.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> list(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(projectService.listByUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody CreateProjectRequest req
    ) {
        return ResponseEntity.ok(projectService.create(user.getId(), req));
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<ProjectDto> get(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getById(projectId));
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("@projectSecurity.isAdmin(principal, #projectId)")
    public ResponseEntity<ProjectDto> update(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest req
    ) {
        return ResponseEntity.ok(projectService.update(projectId, req));
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("@projectSecurity.isAdmin(principal, #projectId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId
    ) {
        projectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }
}
