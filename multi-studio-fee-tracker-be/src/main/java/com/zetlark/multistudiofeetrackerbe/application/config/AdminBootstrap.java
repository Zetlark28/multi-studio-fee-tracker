package com.zetlark.multistudiofeetrackerbe.application.config;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUserRole;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminBootstrap(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminUsername == null || adminUsername.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        if (appUserRepository.existsById(adminUsername)) {
            return;
        }
        AppUser admin = AppUser.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .role(AppUserRole.ADMIN)
                .build();
        appUserRepository.save(admin);
    }
}
