package com.zetlark.multistudiofeetrackerbe.domain.client.controller;

import com.zetlark.multistudiofeetrackerbe.domain.client.service.ClientService;
import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;
import com.zetlark.multistudiofeetrackerbe.domain.client.dto.ClientDto;

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
@RequestMapping("/clients")
public class ClientController extends BaseController<Client, ClientDto, Long> {

    public ClientController(ClientService service) {
        super(service);
    }

    @Override
    @PostMapping
    public ResponseEntity<ClientDto> create(@Valid @RequestBody ClientDto dto) {
        return super.create(dto);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @Valid @RequestBody ClientDto dto) {
        return super.update(id, dto);
    }

    @Override
    @GetMapping
    public ResponseEntity<ResponseList<ClientDto>> findAll(@ModelAttribute ClientDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
