package com.example.Lifepath.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false, unique=true) private String email;
 @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.USER;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
 protected User(){} public User(String n,String e,String p){name=n;email=e;password=p;}
 @PreUpdate void update(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPassword(){return password;} public Role getRole(){return role;} public LocalDateTime getCreatedAt(){return createdAt;}
 public void setName(String v){name=v;} public void setEmail(String v){email=v;} public void setRole(Role v){role=v;}
 public void setPassword(String v){password=v;}
}
