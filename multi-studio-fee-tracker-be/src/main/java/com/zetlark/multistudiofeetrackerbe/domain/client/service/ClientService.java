package com.zetlark.multistudiofeetrackerbe.domain.client.service;

import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;
import com.zetlark.multistudiofeetrackerbe.domain.client.dto.ClientDto;
import com.zetlark.multistudiofeetrackerbe.domain.client.exception.ClientHasActiveServicesException;
import com.zetlark.multistudiofeetrackerbe.domain.client.repository.ClientRepository;
import com.zetlark.multistudiofeetrackerbe.domain.client.mapper.ClientMapper;

import java.util.List;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponsePageable;
import com.zetlark.multistudiofeetrackerbe.application.common.service.BaseServiceImpl;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.repository.AppUserRepository;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.repository.ClientBillableServiceRepository;
import com.zetlark.multistudiofeetrackerbe.application.config.security.CurrentUserProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService extends BaseServiceImpl<Client, ClientDto, Long> {

    private final ClientRepository clientRepository;
    private final ClientBillableServiceRepository clientBillableServiceRepository;
    private final AppUserRepository appUserRepository;
    private final CurrentUserProvider currentUserProvider;

    public ClientService(
            ClientRepository repository,
            ClientMapper mapper,
            ClientBillableServiceRepository clientBillableServiceRepository,
            AppUserRepository appUserRepository,
            CurrentUserProvider currentUserProvider) {
        super(repository, mapper);
        this.clientRepository = repository;
        this.clientBillableServiceRepository = clientBillableServiceRepository;
        this.appUserRepository = appUserRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public ClientDto create(ClientDto dto) {
        Client entity = mapper.toEntity(dto);
        entity.setOwner(appUserRepository.getReferenceById(currentUserProvider.getUsername()));
        Client saved = clientRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ClientDto getById(Long id) {
        return mapper.toDto(resolveClient(id, currentUserProvider.getUsername(), currentUserProvider.isAdmin()));
    }

    @Override
    @Transactional
    public ClientDto update(Long id, ClientDto dto) {
        Client existing = resolveClient(id, currentUserProvider.getUsername(), currentUserProvider.isAdmin());
        mapper.updateEntityFromDto(dto, existing);
        Client saved = clientRepository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        boolean isAdmin = currentUserProvider.isAdmin();
        boolean exists = isAdmin
                ? clientRepository.existsById(id)
                : clientRepository.existsByIdAndOwner_Username(id, currentUserProvider.getUsername());
        if (!exists) {
            throw new EntityNotFoundException("Entity with id: " + id + " not found");
        }
        if (clientBillableServiceRepository.existsByClientId(id)) {
            throw new ClientHasActiveServicesException(
                    "Non è possibile eliminare il cliente, ci sono dei servizi attivi.");
        }
        clientRepository.deleteById(id);
    }

    @Override
    public ResponseList<ClientDto> findAll(ClientDto filter, Pageable pageable) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        Pageable effectivePageable = pageable != null ? pageable : Pageable.unpaged();
        boolean hasNameFilter = filter != null && filter.getName() != null && !filter.getName().isBlank();

        Page<Client> page;
        if (isAdmin) {
            page = hasNameFilter
                    ? clientRepository.findAllByNameContainingIgnoreCase(filter.getName(), effectivePageable)
                    : clientRepository.findAll(effectivePageable);
        } else {
            page = hasNameFilter
                    ? clientRepository.findAllByOwner_UsernameAndNameContainingIgnoreCase(
                            username, filter.getName(), effectivePageable)
                    : clientRepository.findAllByOwner_Username(username, effectivePageable);
        }

        List<ClientDto> data = mapper.toDtoList(page.getContent());

        ResponsePageable<ClientDto> response = new ResponsePageable<>();
        response.setData(data);
        response.setTotalItems(page.getTotalElements());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    private Client resolveClient(Long id, String username, boolean isAdmin) {
        return isAdmin
                ? clientRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"))
                : clientRepository.findByIdAndOwner_Username(id, username)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"));
    }
}
