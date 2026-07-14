package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
   User findByEmail(String email);
}
