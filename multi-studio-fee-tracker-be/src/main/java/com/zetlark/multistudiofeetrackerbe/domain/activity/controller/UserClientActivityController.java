package com.zetlark.multistudiofeetrackerbe.domain.activity.controller;

import com.zetlark.multistudiofeetrackerbe.domain.activity.service.UserClientActivityService;
import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.UserClientActivityDto;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.controller.BaseController;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClientActivityController extends BaseController<UserClientActivity, UserClientActivityDto, Long>
        implements UserClientActivityControllerApi {

    private final UserClientActivityService service;

    public UserClientActivityController(UserClientActivityService service) {
        super(service);
        this.service = service;
    }

    @Override
    public ResponseEntity<List<UserClientActivityDto>> findAllForMonth(Long month) {
        return ResponseEntity.ok(service.findAllForMonth(month));
    }

    @Override
    public ResponseEntity<UserClientActivityDto> create(UserClientActivityDto dto) {
        return super.create(dto);
    }

    @Override
    public ResponseEntity<UserClientActivityDto> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ResponseEntity<UserClientActivityDto> update(Long id, UserClientActivityDto dto) {
        return super.update(id, dto);
    }

    @Override
    public ResponseEntity<ResponseList<UserClientActivityDto>> findAll(UserClientActivityDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }
}
