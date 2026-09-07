package com.zetlark.multistudiofeetrackerbe.domain.admin.controller;

import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.CreateUserRequest;
import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.SetPasswordRequest;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.service.AppUserService;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.dto.AppUserSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserService appUserService;

    public AdminUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public ResponseEntity<List<AppUserSummaryDto>> list() {
        return ResponseEntity.ok(appUserService.listAllUsers());
    }

    @PostMapping
    public ResponseEntity<AppUserSummaryDto> create(@Valid @RequestBody CreateUserRequest request) {
        AppUser user = appUserService.createUserAsAdmin(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AppUserSummaryDto(user.getUsername(), user.getRole()));
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> setPassword(@PathVariable String username, @Valid @RequestBody SetPasswordRequest request) {
        appUserService.setUserPassword(username, request.getPassword());
        return ResponseEntity.ok().build();
    }
}
