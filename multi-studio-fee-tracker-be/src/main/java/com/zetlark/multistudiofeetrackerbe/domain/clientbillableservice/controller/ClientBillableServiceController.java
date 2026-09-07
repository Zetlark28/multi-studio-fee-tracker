package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.controller;

import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.service.ClientBillableServiceService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto.ClientBillableServiceDto;

import com.zetlark.multistudiofeetrackerbe.application.common.controller.BaseController;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientBillableServiceController extends BaseController<ClientBillableService, ClientBillableServiceDto, Long>
        implements ClientBillableServiceControllerApi {

    public ClientBillableServiceController(ClientBillableServiceService service) {
        super(service);
    }

    @Override
    public ResponseEntity<ClientBillableServiceDto> create(ClientBillableServiceDto dto) {
        return super.create(dto);
    }

    @Override
    public ResponseEntity<ClientBillableServiceDto> getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ResponseEntity<ClientBillableServiceDto> update(Long id, ClientBillableServiceDto dto) {
        return super.update(id, dto);
    }

    @Override
    public ResponseEntity<ResponseList<ClientBillableServiceDto>> findAll(ClientBillableServiceDto filter, Pageable pageable) {
        return super.findAll(filter, pageable);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }
}
