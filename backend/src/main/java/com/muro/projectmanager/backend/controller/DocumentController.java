package com.muro.projectmanager.backend.controller;

import com.muro.projectmanager.backend.dto.DocumentDto;
import com.muro.projectmanager.backend.model.Document;
import com.muro.projectmanager.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService docService;

    @GetMapping
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<List<DocumentDto>> list(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(docService.list(projectId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<DocumentDto> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) throws IOException {
        DocumentDto dto = docService.store(projectId, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long documentId
    ) {
        docService.delete(projectId, documentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{documentId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<DocumentDto> updateDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            @RequestBody DocumentDto req) {
        DocumentDto updated = docService.update(projectId, documentId, req);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("@projectSecurity.canAccess(principal, #projectId)")
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable Long projectId,
            @PathVariable Long documentId) {
        Document d = docService.get(projectId, documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(d.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + d.getFileName() + "\"")
                .body(new ByteArrayResource(d.getData()));
    }
}