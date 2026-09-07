package com.zetlark.multistudiofeetrackerbe.domain.activity.mapper;

import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;
import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivityServiceEntry;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.ServiceSelectionDto;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.UserClientActivityDto;

import java.util.ArrayList;
import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.mapper.BaseMapper;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.repository.ClientBillableServiceRepository;
import com.zetlark.multistudiofeetrackerbe.domain.client.repository.ClientRepository;
import org.springframework.stereotype.Component;

@Component
public class UserClientActivityMapper implements BaseMapper<UserClientActivity, UserClientActivityDto> {

    private final ClientRepository clientRepository;
    private final ClientBillableServiceRepository clientBillableServiceRepository;

    public UserClientActivityMapper(
            ClientRepository clientRepository, ClientBillableServiceRepository clientBillableServiceRepository) {
        this.clientRepository = clientRepository;
        this.clientBillableServiceRepository = clientBillableServiceRepository;
    }

    @Override
    public UserClientActivityDto toDto(UserClientActivity entity) {
        if (entity == null) {
            return null;
        }
        UserClientActivityDto dto = new UserClientActivityDto();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClient() != null ? entity.getClient().getId() : null);
        dto.setServices(entity.getServiceEntries() == null
                ? List.of()
                : entity.getServiceEntries().stream()
                        .map(entry -> new ServiceSelectionDto(entry.getService().getId(), entry.getQuantity()))
                        .toList());
        dto.setDate(entity.getDate());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        dto.setFee(entity.getFee());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    @Override
    public UserClientActivity toEntity(UserClientActivityDto dto) {
        if (dto == null) {
            return null;
        }
        UserClientActivity entity = new UserClientActivity();
        entity.setId(dto.getId());
        entity.setClient(dto.getClientId() != null ? clientRepository.getReferenceById(dto.getClientId()) : null);
        entity.setDate(dto.getDate());
        entity.setQuantity(dto.getQuantity());
        entity.setPrice(dto.getPrice());
        entity.setFee(dto.getFee());
        entity.setServiceEntries(new ArrayList<>());
        applyServiceEntries(entity, dto.getServices());
        return entity;
    }

    @Override
    public List<UserClientActivityDto> toDtoList(List<UserClientActivity> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    @Override
    public List<UserClientActivity> toEntityList(List<UserClientActivityDto> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }

    @Override
    public void updateEntityFromDto(UserClientActivityDto dto, UserClientActivity entity) {
        entity.setClient(dto.getClientId() != null ? clientRepository.getReferenceById(dto.getClientId()) : null);
        entity.setDate(dto.getDate());
        entity.setQuantity(dto.getQuantity());
        entity.setPrice(dto.getPrice());
        entity.setFee(dto.getFee());
        if (entity.getServiceEntries() == null) {
            entity.setServiceEntries(new ArrayList<>());
        } else {
            entity.getServiceEntries().clear();
        }
        applyServiceEntries(entity, dto.getServices());
    }

    private void applyServiceEntries(UserClientActivity entity, List<ServiceSelectionDto> selections) {
        if (selections == null) {
            return;
        }
        for (ServiceSelectionDto selection : selections) {
            UserClientActivityServiceEntry entry = new UserClientActivityServiceEntry();
            entry.setActivity(entity);
            entry.setService(clientBillableServiceRepository.getReferenceById(selection.getServiceId()));
            entry.setQuantity(selection.getQuantity());
            entity.getServiceEntries().add(entry);
        }
    }
}
