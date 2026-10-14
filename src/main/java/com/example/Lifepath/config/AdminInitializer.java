
package com.example.Lifepath.config;

import com.example.Lifepath.entity.Role;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository users,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@lifepath.com";
            String adminPassword = "Admin@123";

            if (users.existsByEmail(adminEmail)) {
                return;
            }

            User admin = new User(
                    "LifePath Admin",
                    adminEmail,
                    passwordEncoder.encode(adminPassword)
            );

            admin.setRole(Role.ADMIN);

            users.save(admin);

            System.out.println("======================================");
            System.out.println("LifePath initial ADMIN created");
            System.out.println("Email: " + adminEmail);
            System.out.println("======================================");
        };
    }
}
