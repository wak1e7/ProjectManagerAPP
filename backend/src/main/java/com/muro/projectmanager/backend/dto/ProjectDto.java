package com.muro.projectmanager.backend.dto;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectDto {
    private Long id;
    private String title;
    private String description;
}
