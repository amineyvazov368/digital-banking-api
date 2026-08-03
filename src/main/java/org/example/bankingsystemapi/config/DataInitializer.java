package org.example.bankingsystemapi.config;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.Role;
import org.example.bankingsystemapi.model.enums.UserStatus;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@bank.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setSurname("System");
            admin.setEmail("admin@bank.com");
            admin.setPassword(passwordEncoder.encode("Admin12345!"));
            admin.setRole(Role.ADMIN);
            admin.setUserStatus(UserStatus.ACTIVE);

            userRepository.save(admin);
            System.out.println("==================================================");
            System.out.println(">>> ADMİN İSTİFADƏÇİSİ BAZADA UĞURLA YARADILDI! <<<");
            System.out.println("Email: admin@bank.com");
            System.out.println("Şifrə: Admin12345!");
            System.out.println("==================================================");
        }
    }
}