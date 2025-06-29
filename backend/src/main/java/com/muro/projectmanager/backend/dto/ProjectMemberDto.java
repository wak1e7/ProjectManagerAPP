package com.muro.projectmanager.backend.dto;

import com.muro.projectmanager.backend.model.ProjectRoleName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDto {
    private Long userId;
    private String email;
    private ProjectRoleName role;
}
