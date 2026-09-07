package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.service;

import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto.ClientBillableServiceDto;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.exception.DuplicateServiceNameException;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.repository.ClientBillableServiceRepository;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.mapper.ClientBillableServiceMapper;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponsePageable;
import com.zetlark.multistudiofeetrackerbe.application.common.service.BaseServiceImpl;
import com.zetlark.multistudiofeetrackerbe.domain.client.repository.ClientRepository;
import com.zetlark.multistudiofeetrackerbe.application.config.security.CurrentUserProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientBillableServiceService extends BaseServiceImpl<ClientBillableService, ClientBillableServiceDto, Long> {

    private final ClientBillableServiceRepository repository;
    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public ClientBillableServiceService(
            ClientBillableServiceRepository repository,
            ClientBillableServiceMapper mapper,
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider) {
        super(repository, mapper);
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public ClientBillableServiceDto create(ClientBillableServiceDto dto) {
        String username = currentUserProvider.getUsername();
        assertClientOwnedByCurrentUser(dto.getClientId(), username, currentUserProvider.isAdmin());
        assertNameIsUniqueForClient(dto.getClientId(), dto.getName(), null);
        return super.create(dto);
    }

    @Override
    public ClientBillableServiceDto getById(Long id) {
        return mapper.toDto(resolveEntity(id, currentUserProvider.getUsername(), currentUserProvider.isAdmin()));
    }

    @Override
    @Transactional
    public ClientBillableServiceDto update(Long id, ClientBillableServiceDto dto) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        assertClientOwnedByCurrentUser(dto.getClientId(), username, isAdmin);
        ClientBillableService existing = resolveEntity(id, username, isAdmin);
        assertNameIsUniqueForClient(dto.getClientId(), dto.getName(), id);
        mapper.updateEntityFromDto(dto, existing);
        ClientBillableService saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        boolean isAdmin = currentUserProvider.isAdmin();
        boolean exists = isAdmin
                ? repository.existsById(id)
                : repository.existsByIdAndClient_Owner_Username(id, currentUserProvider.getUsername());
        if (!exists) {
            throw new EntityNotFoundException("Entity with id: " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public ResponseList<ClientBillableServiceDto> findAll(ClientBillableServiceDto filter, Pageable pageable) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        Pageable effectivePageable = pageable != null ? pageable : Pageable.unpaged();
        boolean hasClientFilter = filter != null && filter.getClientId() != null;

        Page<ClientBillableService> page;
        if (isAdmin) {
            page = hasClientFilter
                    ? repository.findAllByClient_Id(filter.getClientId(), effectivePageable)
                    : repository.findAll(effectivePageable);
        } else {
            page = hasClientFilter
                    ? repository.findAllByClient_IdAndClient_Owner_Username(filter.getClientId(), username, effectivePageable)
                    : repository.findAllByClient_Owner_Username(username, effectivePageable);
        }

        List<ClientBillableServiceDto> data = mapper.toDtoList(page.getContent());

        ResponsePageable<ClientBillableServiceDto> response = new ResponsePageable<>();
        response.setData(data);
        response.setTotalItems(page.getTotalElements());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    private ClientBillableService resolveEntity(Long id, String username, boolean isAdmin) {
        return isAdmin
                ? repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"))
                : repository.findByIdAndClient_Owner_Username(id, username)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"));
    }

    private void assertClientOwnedByCurrentUser(Long clientId, String username, boolean isAdmin) {
        boolean owned = clientId != null
                && (isAdmin ? clientRepository.existsById(clientId) : clientRepository.existsByIdAndOwner_Username(clientId, username));
        if (!owned) {
            throw new EntityNotFoundException("Client with id: " + clientId + " not found");
        }
    }

    private void assertNameIsUniqueForClient(Long clientId, String name, Long excludingId) {
        boolean nameAlreadyUsed = excludingId == null
                ? repository.existsByClientIdAndNameIgnoreCase(clientId, name)
                : repository.existsByClientIdAndNameIgnoreCaseAndIdNot(clientId, name, excludingId);

        if (nameAlreadyUsed) {
            throw new DuplicateServiceNameException(
                    "Esiste già un servizio chiamato \"" + name + "\" per questo cliente.");
        }
    }
}
