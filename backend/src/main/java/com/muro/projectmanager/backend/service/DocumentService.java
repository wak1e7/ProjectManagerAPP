package com.muro.projectmanager.backend.service;

import com.muro.projectmanager.backend.dto.DocumentDto;
import com.muro.projectmanager.backend.model.*;
import com.muro.projectmanager.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository docRepo;
    private final ProjectRepository projectRepo;

    public List<DocumentDto> list(Long projectId) {
        return docRepo.findAllByProjectId(projectId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentDto store(Long projectId, MultipartFile file) throws IOException {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));
        Document doc = Document.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .project(project)
                .build();
        return toDto(docRepo.save(doc));
    }

    @Transactional
    public void delete(Long projectId, Long documentId) {
        Document doc = docRepo.findById(documentId)
                .filter(d -> d.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
        docRepo.delete(doc);
    }

    @Transactional
    public DocumentDto update(Long projectId,
                              Long documentId,
                              DocumentDto req) {
        Document doc = docRepo.findById(documentId)
                .filter(d -> d.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        if (req.getFileName() != null && !req.getFileName().isBlank()) {
            doc.setFileName(req.getFileName());
        }
        if (req.getFileType() != null && !req.getFileType().isBlank()) {
            doc.setFileType(req.getFileType());
        }
        if (req.getUpdatedAt() != null) {
            doc.setUpdatedAt(req.getUpdatedAt());
        } else {
            doc.setUpdatedAt(Instant.now());
        }
        if (req.getVersion() != null && !req.getVersion().isBlank()) {
            doc.setVersion(req.getVersion());
        }

        Document saved = docRepo.save(doc);
        return toDto(saved);
    }

    public Document get(Long projectId, Long documentId) {
        return docRepo.findById(documentId)
                .filter(d -> d.getProject().getId().equals(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
    }

    private DocumentDto toDto(Document d) {
        return DocumentDto.builder()
                .id(d.getId())
                .fileName(d.getFileName())
                .fileType(d.getFileType())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .version(d.getVersion())
                .build();
    }
}