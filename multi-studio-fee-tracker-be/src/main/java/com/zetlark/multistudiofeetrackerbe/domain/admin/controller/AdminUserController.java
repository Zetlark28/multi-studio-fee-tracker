package com.zetlark.multistudiofeetrackerbe.domain.admin.controller;

import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.CreateUserRequest;
import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.SetPasswordRequest;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.service.AppUserService;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.dto.AppUserSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUserController implements AdminUserControllerApi {

    private final AppUserService appUserService;

    public AdminUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public ResponseEntity<List<AppUserSummaryDto>> list() {
        return ResponseEntity.ok(appUserService.listAllUsers());
    }

    @Override
    public ResponseEntity<AppUserSummaryDto> create(CreateUserRequest request) {
        AppUser user = appUserService.createUserAsAdmin(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AppUserSummaryDto(user.getUsername(), user.getRole()));
    }

    @Override
    public ResponseEntity<Void> setPassword(String username, SetPasswordRequest request) {
        appUserService.setUserPassword(username, request.getPassword());
        return ResponseEntity.ok().build();
    }
}
