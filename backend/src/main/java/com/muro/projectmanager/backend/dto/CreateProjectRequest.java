package com.muro.projectmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateProjectRequest {
    @NotBlank private String title;
    private String description;
}
