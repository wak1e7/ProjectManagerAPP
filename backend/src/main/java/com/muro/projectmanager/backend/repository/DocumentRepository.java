package com.muro.projectmanager.backend.repository;

import com.muro.projectmanager.backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByProjectId(Long projectId);
}
