package com.zetlark.multistudiofeetrackerbe.domain.client.mapper;

import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;
import com.zetlark.multistudiofeetrackerbe.domain.client.dto.ClientDto;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper implements BaseMapper<Client, ClientDto> {

    @Override
    public ClientDto toDto(Client entity) {
        if (entity == null) {
            return null;
        }
        ClientDto dto = new ClientDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFee(entity.getFee());
        dto.setType(entity.getType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    @Override
    public Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setFee(dto.getFee());
        entity.setType(dto.getType());
        return entity;
    }

    @Override
    public List<ClientDto> toDtoList(List<Client> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    @Override
    public List<Client> toEntityList(List<ClientDto> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }

    @Override
    public void updateEntityFromDto(ClientDto dto, Client entity) {
        entity.setName(dto.getName());
        entity.setFee(dto.getFee());
        entity.setType(dto.getType());
    }
}
