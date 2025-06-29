package com.muro.projectmanager.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProjectRoleName role;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean accepted;

    @PrePersist
    public void prePersist() {
        this.token = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plusSeconds(60 * 60 * 24 * 7); // 7 días
        this.accepted = false;
    }
}
