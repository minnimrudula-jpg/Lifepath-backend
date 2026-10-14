package com.example.Lifepath.repository;

import com.example.Lifepath.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @org.springframework.data.jpa.repository.Query("select function('DATE', u.createdAt), count(u) "
            + "from User u group by function('DATE', u.createdAt) order by function('DATE', u.createdAt)")
    java.util.List<Object[]> countRegistrationsByDate();
}
