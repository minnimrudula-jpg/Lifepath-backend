package com.example.Lifepath.repository;

import com.example.Lifepath.entity.UserDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {
    List<UserDocument> findAllByUserIdAndDocumentIdOrderByIdAsc(Long userId, Long documentId);
    boolean existsByDocumentId(Long documentId);
}
