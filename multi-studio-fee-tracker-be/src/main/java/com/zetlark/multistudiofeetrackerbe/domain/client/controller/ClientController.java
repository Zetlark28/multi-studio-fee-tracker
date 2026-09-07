package com.zetlark.multistudiofeetrackerbe.domain.client.controller;

import com.zetlark.multistudiofeetrackerbe.domain.client.service.ClientService;
import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;
import com.zetlark.multistudiofeetrackerbe.domain.client.dto.ClientDto;

import com.zetlark.multistudiofeetrackerbe.application.common.controller.BaseController;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController extends BaseController<Client, ClientDto, Long> implements ClientControllerApi {

    public ClientController(ClientService service) {
        super(service);
    }

    @Override
    public ResponseEntity<ClientDto> create(ClientDto dto) {
        return super.create(dto);
    }

    @Override
    public ResponseEntity<ClientDto> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ResponseEntity<ClientDto> update(Long id, ClientDto dto) {
        return super.update(id, dto);
    }

    @Override
    public ResponseEntity<ResponseList<ClientDto>> findAll(ClientDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }
}
