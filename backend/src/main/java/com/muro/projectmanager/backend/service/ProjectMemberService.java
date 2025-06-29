package com.muro.projectmanager.backend.service;


import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.model.*;
import com.muro.projectmanager.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final ProjectMemberRepository pmRepo;
    private final InvitationRepository invRepo;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void addOrInviteMember(Long projectId, String email, ProjectRoleName role) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));

        Optional<User> optUser = userRepo.findByEmail(email);
        if (optUser.isPresent()) {
            User u = optUser.get();
            ProjectMemberId pmId = new ProjectMemberId(projectId, u.getId());
            if (pmRepo.existsById(pmId)) {
                throw new IllegalStateException("Ya es miembro");
            }
            ProjectMember pm = ProjectMember.builder()
                    .id(pmId)
                    .project(project)
                    .user(u)
                    .role(role)
                    .build();
            pmRepo.save(pm);
        } else {
            Invitation inv = Invitation.builder()
                    .project(project)
                    .email(email)
                    .role(role)
                    .build();
            invRepo.save(inv);
            String link = frontendUrl + "/invitations/accept?token=" + inv.getToken();
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Invitación al proyecto \"" + project.getTitle() + "\"");
            msg.setText("Te han invitado como " + role +
                    " en el proyecto \"" + project.getTitle() +
                    "\".\nPulsa aquí para unirte:\n" + link);
            mailSender.send(msg);
        }
    }

    @Transactional
    public void acceptInvitation(String token, Long userId) {
        Invitation inv = invRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitación inválida"));
        if (inv.isAccepted() || inv.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Invitación expirada o ya usada");
        }
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));
        ProjectMemberId pmId = new ProjectMemberId(inv.getProject().getId(), userId);
        if (pmRepo.existsById(pmId)) {
            throw new IllegalStateException("Ya es miembro");
        }
        ProjectMember pm = ProjectMember.builder()
                .id(pmId)
                .project(inv.getProject())
                .user(u)
                .role(inv.getRole())
                .build();
        pmRepo.save(pm);

        inv.setAccepted(true);
        invRepo.save(inv);
    }


    @Transactional
    public void addCreatorAsAdmin(Long projectId, Long creatorId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));
        User u = userRepo.findById(creatorId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));
        ProjectMember pm = ProjectMember.builder()
                .id(new ProjectMemberId(projectId, creatorId))
                .project(p)
                .user(u)
                .role(ProjectRoleName.PROJECT_ADMIN)
                .build();
        pmRepo.save(pm);
    }


    @Transactional
    public void assignRole(Long projectId, Long userId, ProjectRoleName role) {
        ProjectMemberId id = new ProjectMemberId(projectId, userId);
        ProjectMember pm = pmRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado"));
        pm.setRole(role);
        pmRepo.save(pm);
    }

    public List<ProjectMember> listMembers(Long projectId) {
        return pmRepo.findAllByProjectId(projectId);
    }

    @Transactional
    public void removeMember(Long projectId, Long userId) {
        ProjectMemberId id = new ProjectMemberId(projectId, userId);
        ProjectMember pm = pmRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado"));
        pmRepo.delete(pm);
    }
}
