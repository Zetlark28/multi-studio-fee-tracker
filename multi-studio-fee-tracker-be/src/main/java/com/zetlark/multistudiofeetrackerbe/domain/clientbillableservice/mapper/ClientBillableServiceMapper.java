package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.mapper;

import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto.ClientBillableServiceDto;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.mapper.BaseMapper;
import com.zetlark.multistudiofeetrackerbe.domain.client.repository.ClientRepository;
import org.springframework.stereotype.Component;

@Component
public class ClientBillableServiceMapper implements BaseMapper<ClientBillableService, ClientBillableServiceDto> {

    private final ClientRepository clientRepository;

    public ClientBillableServiceMapper(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientBillableServiceDto toDto(ClientBillableService entity) {
        if (entity == null) {
            return null;
        }
        ClientBillableServiceDto dto = new ClientBillableServiceDto();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null);
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    @Override
    public ClientBillableService toEntity(ClientBillableServiceDto dto) {
        if (dto == null) {
            return null;
        }
        ClientBillableService entity = new ClientBillableService();
        entity.setId(dto.getId());
        entity.setClient(dto.getClientId() != null ? clientRepository.getReferenceById(dto.getClientId()) : null);
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    @Override
    public List<ClientBillableServiceDto> toDtoList(List<ClientBillableService> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    @Override
    public List<ClientBillableService> toEntityList(List<ClientBillableServiceDto> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }

    @Override
    public void updateEntityFromDto(ClientBillableServiceDto dto, ClientBillableService entity) {
        entity.setClient(dto.getClientId() != null ? clientRepository.getReferenceById(dto.getClientId()) : null);
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
    }
}
