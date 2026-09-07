package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.controller;

import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.service.ClientBillableServiceService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto.ClientBillableServiceDto;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client-billable-services")
public class ClientBillableServiceController extends BaseController<ClientBillableService, ClientBillableServiceDto, Long> {

    public ClientBillableServiceController(ClientBillableServiceService service) {
        super(service);
    }

    @Override
    @PostMapping
    public ResponseEntity<ClientBillableServiceDto> create(@Valid @RequestBody ClientBillableServiceDto dto) {
        return super.create(dto);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ClientBillableServiceDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ClientBillableServiceDto> update(@PathVariable Long id, @Valid @RequestBody ClientBillableServiceDto dto) {
        return super.update(id, dto);
    }

    @Override
    @GetMapping
    public ResponseEntity<ResponseList<ClientBillableServiceDto>> findAll(@ModelAttribute ClientBillableServiceDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
