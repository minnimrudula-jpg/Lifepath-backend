package com.example.Lifepath.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "user_preparation_tasks", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "preparation_task_id" }))
public class UserPreparationTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne @JoinColumn(name = "preparation_task_id", nullable = false) private PreparationTask task;
    private String status;
    protected UserPreparationTask() {}
    public UserPreparationTask(User user, PreparationTask task, String status) { this.user = user; this.task = task; this.status = status; }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public PreparationTask getTask() { return task; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
