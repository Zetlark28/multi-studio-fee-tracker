package com.zetlark.multistudiofeetrackerbe.domain.activity.controller;

import com.zetlark.multistudiofeetrackerbe.domain.activity.service.UserClientActivityService;
import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.UserClientActivityDto;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.controller.BaseController;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-client-activities")
public class UserClientActivityController extends BaseController<UserClientActivity, UserClientActivityDto, Long> {

    private final UserClientActivityService service;

    public UserClientActivityController(UserClientActivityService service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/history")
    public ResponseEntity<List<UserClientActivityDto>> findAllForMonth(@RequestParam Long month) {
        return ResponseEntity.ok(service.findAllForMonth(month));
    }

    @Override
    @PostMapping
    public ResponseEntity<UserClientActivityDto> create(@Valid @RequestBody UserClientActivityDto dto) {
        return super.create(dto);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserClientActivityDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserClientActivityDto> update(@PathVariable Long id, @Valid @RequestBody UserClientActivityDto dto) {
        return super.update(id, dto);
    }

    @Override
    @GetMapping
    public ResponseEntity<ResponseList<UserClientActivityDto>> findAll(@ModelAttribute UserClientActivityDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
