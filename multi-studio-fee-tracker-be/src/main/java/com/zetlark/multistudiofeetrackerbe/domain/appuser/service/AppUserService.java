package com.zetlark.multistudiofeetrackerbe.domain.appuser.service;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUserRole;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.dto.AppUserSummaryDto;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.exception.UsernameAlreadyExistsException;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.repository.AppUserRepository;

import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.CurrentUser;
import com.zetlark.multistudiofeetrackerbe.application.config.security.CurrentUserProvider;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public AppUserService(AppUserRepository repository, PasswordEncoder passwordEncoder,
            CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
    }

    public CurrentUser getCurrentUser() {
        String username = currentUserProvider.getUsername();
        AppUser appUser = repository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
        return new CurrentUser(appUser.getUsername(), appUser.getRole() == AppUserRole.ADMIN);
    }

    public AppUser register(String username, String rawPassword) {
        if (repository.existsById(username)) {
            throw new UsernameAlreadyExistsException("Username \"" + username + "\" già in uso.");
        }
        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        return repository.save(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        CurrentUser currentUser = getCurrentUser();
        if(!currentUser.getIsAdmin() && !currentUser.getUsername().equals(username)) {
                throw new AccessDeniedException("Credenziali non valide");
            }

        AppUser user = repository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Credenziali non valide");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    public AppUser findByUsername(String username) {
        return repository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
    }

    public List<AppUserSummaryDto> listAllUsers() {
        requireAdmin();
        return repository.findAll().stream()
                .map(user -> new AppUserSummaryDto(user.getUsername(), user.getRole()))
                .toList();
    }

    public AppUser createUserAsAdmin(String username, String rawPassword) {
        requireAdmin();
        return register(username, rawPassword);
    }

    public void setUserPassword(String targetUsername, String newPassword) {
        requireAdmin();
        AppUser user = repository.findById(targetUsername)
                .orElseThrow(() -> new EntityNotFoundException("Utente \"" + targetUsername + "\" non trovato."));
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    private void requireAdmin() {
        if (!currentUserProvider.isAdmin()) {
            throw new AccessDeniedException("Accesso riservato agli amministratori.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = repository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
        List<GrantedAuthority> authorities = user.getRole() == AppUserRole.ADMIN
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of();
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
