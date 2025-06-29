package com.muro.projectmanager.backend.service;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.model.Project;
import com.muro.projectmanager.backend.model.Task;
import com.muro.projectmanager.backend.model.TaskStatus;
import com.muro.projectmanager.backend.model.User;
import com.muro.projectmanager.backend.repository.ProjectRepository;
import com.muro.projectmanager.backend.repository.TaskRepository;
import com.muro.projectmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public List<TaskDto> list(Long projectId) {
        return taskRepo.findAllByProjectId(projectId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto create(Long projectId, CreateTaskRequest req) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));
        Task t = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .status(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO)
                .project(p)
                .build();
        if (req.getDueDate() != null) {
            t.setDueDate(LocalDate.parse(req.getDueDate()));
        }
        if (req.getAssigneeIds() != null) {
            Set<User> users = new HashSet<>(userRepo.findAllById(req.getAssigneeIds()));
            t.setAssignees(users);
        }
        return toDto(taskRepo.save(t));
    }

    public TaskDto get(Long projectId, Long taskId) {
        Task t = taskRepo.findById(taskId)
                .filter(x -> x.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        return toDto(t);
    }

    @Transactional
    public TaskDto update(Long projectId, Long taskId, TaskDto req) {
        Task t = taskRepo.findById(taskId)
                .filter(x -> x.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        if (req.getTitle() != null) {
            t.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            t.setDescription(req.getDescription());
        }
        if (req.getStatus() != null) {
            t.setStatus(req.getStatus());
        }
        if (req.getDueDate() != null && !req.getDueDate().isBlank()) {
            t.setDueDate(LocalDate.parse(req.getDueDate()));
        }
        if (req.getAssigneeIds() != null) {
            Set<User> users = userRepo.findAllById(req.getAssigneeIds())
                    .stream().collect(Collectors.toSet());
            t.setAssignees(users);
        }

        return toDto(t);
    }

    @Transactional
    public void delete(Long projectId, Long taskId) {
        Task t = taskRepo.findById(taskId)
                .filter(x -> x.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        taskRepo.delete(t);
    }

    private TaskDto toDto(Task t) {
        return TaskDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .dueDate(t.getDueDate() != null ? t.getDueDate().toString() : null)
                .assigneeIds(t.getAssignees().stream().map(User::getId).toList())
                .build();
    }
}