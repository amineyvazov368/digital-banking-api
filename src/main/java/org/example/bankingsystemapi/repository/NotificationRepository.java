package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Notification;
import org.example.bankingsystemapi.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);

//    Long countByRoleAndIsReadFalse(Role role);
}
