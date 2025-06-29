package com.muro.projectmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import com.muro.projectmanager.backend.model.TaskStatus;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank private String title;
    private String description;
    private TaskStatus status;
    private String dueDate;
    private List<Long> assigneeIds;
}
