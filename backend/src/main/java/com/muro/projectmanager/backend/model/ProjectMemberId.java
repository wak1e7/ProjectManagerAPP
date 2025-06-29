package com.muro.projectmanager.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ProjectMemberId implements Serializable {
    private Long projectId;
    private Long userId;
}
