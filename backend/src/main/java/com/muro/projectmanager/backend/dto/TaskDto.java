package com.muro.projectmanager.backend.dto;

import com.muro.projectmanager.backend.model.TaskStatus;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String dueDate;
    private List<Long> assigneeIds;
}
