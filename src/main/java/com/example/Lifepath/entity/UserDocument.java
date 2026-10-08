package com.example.Lifepath.entity;
import jakarta.persistence.*;
@Entity @Table(name="user_documents") public class UserDocument { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne @JoinColumn(name="user_id",nullable=false) private User user; @ManyToOne @JoinColumn(name="document_id",nullable=false) private Document document; private String status; private String fileUrl; protected UserDocument(){} }
