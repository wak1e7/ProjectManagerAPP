package com.muro.projectmanager.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectMember {
    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProjectRoleName role;
}
