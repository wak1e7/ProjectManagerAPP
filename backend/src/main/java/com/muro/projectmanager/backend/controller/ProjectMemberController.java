package com.muro.projectmanager.backend.controller;

import com.muro.projectmanager.backend.dto.ProjectMemberDto;
import com.muro.projectmanager.backend.model.ProjectMember;
import com.muro.projectmanager.backend.model.ProjectRoleName;
import com.muro.projectmanager.backend.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService memberService;

    @PostMapping
    @PreAuthorize("@projectSecurity.isAdmin(principal, #projectId)")
    public ResponseEntity<Void> addOrInvite(
            @PathVariable Long projectId,
            @RequestParam String email,
            @RequestParam(defaultValue = "PROJECT_MEMBER") ProjectRoleName role) {
        memberService.addOrInviteMember(projectId, email, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> accept(
            @PathVariable Long projectId,
            @RequestParam String token,
            @RequestParam Long userId) {
        memberService.acceptInvitation(token, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("@projectSecurity.isAdmin(principal, #projectId)")
    public ResponseEntity<Void> changeRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam ProjectRoleName role) {
        memberService.assignRole(projectId, userId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<List<ProjectMemberDto>> list(
            @PathVariable Long projectId) {
        List<ProjectMember> list = memberService.listMembers(projectId);
        List<ProjectMemberDto> dtos = list.stream()
                .map(pm -> ProjectMemberDto.builder()
                        .userId(pm.getUser().getId())
                        .email(pm.getUser().getEmail())
                        .role(pm.getRole())
                        .build())
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@projectSecurity.isAdmin(principal, #projectId)")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        memberService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
