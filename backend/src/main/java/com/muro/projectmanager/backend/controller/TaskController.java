package com.muro.projectmanager.backend.controller;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<List<TaskDto>> list(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(taskService.list(projectId));
    }

    @PostMapping
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<TaskDto> create(
            @PathVariable Long projectId,
            @RequestBody CreateTaskRequest req
    ) {
        return ResponseEntity.ok(taskService.create(projectId, req));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<TaskDto> get(
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.get(projectId, taskId));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<TaskDto> update(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskDto req
    ) {
        return ResponseEntity.ok(taskService.update(projectId, taskId, req));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        taskService.delete(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
