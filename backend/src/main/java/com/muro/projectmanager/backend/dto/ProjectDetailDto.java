package com.muro.projectmanager.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailDto {
    private Long id;
    private String title;
    private String description;
    private List<ProjectMemberDto> members;
    private List<TaskDto> tasks;
}
