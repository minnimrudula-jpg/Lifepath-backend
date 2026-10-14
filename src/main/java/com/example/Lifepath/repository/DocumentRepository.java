package com.example.Lifepath.repository;

import com.example.Lifepath.entity.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByNameIgnoreCase(String name);
}
