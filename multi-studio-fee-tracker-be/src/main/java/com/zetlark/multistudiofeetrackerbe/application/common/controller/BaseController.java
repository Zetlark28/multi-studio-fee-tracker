package com.zetlark.multistudiofeetrackerbe.application.common.controller;


import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.entity.BaseEntity;
import com.zetlark.multistudiofeetrackerbe.application.common.service.BaseServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<E extends BaseEntity, D extends BaseDto, I> {

    protected final BaseServiceImpl<E, D,I> service;

    protected BaseController(BaseServiceImpl<E, D,I> service) {
        this.service = service;
    }

    public ResponseEntity<D> create(@Valid @RequestBody D dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    public ResponseEntity<D> getById(I id) {
        return ResponseEntity.ok(service.getById(id));
    }

    public ResponseEntity<D> update(I id, @Valid @RequestBody D dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    public ResponseEntity<ResponseList<D>> findAll(D filter, Pageable pageable) {
        return ResponseEntity.ok(service.findAll(filter, pageable));
    }

    public ResponseEntity<Void> delete(I id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    public boolean existsById(I id) {
        return service.existsById(id);
    }

}