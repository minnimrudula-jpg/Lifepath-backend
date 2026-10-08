package com.example.Lifepath.entity;
import jakarta.persistence.*;
@Entity @Table(name="user_preparation_tasks",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","preparation_task_id"})) public class UserPreparationTask { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne @JoinColumn(name="user_id",nullable=false) private User user; @ManyToOne @JoinColumn(name="preparation_task_id",nullable=false) private PreparationTask task; private String status; protected UserPreparationTask(){} }
