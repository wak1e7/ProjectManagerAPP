package com.muro.projectmanager.backend.dto;

import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Instant createdAt;
    private Instant updatedAt;
    private String version;
}
