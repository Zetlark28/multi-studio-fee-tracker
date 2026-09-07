package com.zetlark.multistudiofeetrackerbe.domain.auth.controller;

import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.AuthResponse;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.ChangePasswordRequest;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.LoginRequest;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.RegisterRequest;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUserRole;
import com.zetlark.multistudiofeetrackerbe.application.config.security.JwtService;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AppUserService appUserService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.appUserService = appUserService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AppUser user = appUserService.register(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername(), user.getRole() == AppUserRole.ADMIN));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        AppUser user = appUserService.findByUsername(request.getUsername());
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole() == AppUserRole.ADMIN));
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {

        appUserService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
