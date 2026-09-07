package com.zetlark.multistudiofeetrackerbe.domain.activity.service;

import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.UserClientActivityDto;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.ServiceSelectionDto;
import com.zetlark.multistudiofeetrackerbe.domain.activity.exception.InvalidActivityException;
import com.zetlark.multistudiofeetrackerbe.domain.activity.repository.UserClientActivityRepository;
import com.zetlark.multistudiofeetrackerbe.domain.activity.mapper.UserClientActivityMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponsePageable;
import com.zetlark.multistudiofeetrackerbe.application.common.service.BaseServiceImpl;
import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;
import com.zetlark.multistudiofeetrackerbe.domain.client.entity.FeeType;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.repository.ClientBillableServiceRepository;
import com.zetlark.multistudiofeetrackerbe.domain.client.repository.ClientRepository;
import com.zetlark.multistudiofeetrackerbe.application.config.security.CurrentUserProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserClientActivityService extends BaseServiceImpl<UserClientActivity, UserClientActivityDto, Long> {

    private final UserClientActivityRepository repository;
    private final ClientRepository clientRepository;
    private final ClientBillableServiceRepository clientBillableServiceRepository;
    private final CurrentUserProvider currentUserProvider;

    public UserClientActivityService(
            UserClientActivityRepository repository,
            UserClientActivityMapper mapper,
            ClientRepository clientRepository,
            ClientBillableServiceRepository clientBillableServiceRepository,
            CurrentUserProvider currentUserProvider) {
        super(repository, mapper);
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.clientBillableServiceRepository = clientBillableServiceRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public UserClientActivityDto create(UserClientActivityDto dto) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        Client client = resolveClient(dto.getClientId(), username, isAdmin);
        applyFeeCalculation(dto, client, username, isAdmin);
        return super.create(dto);
    }

    @Override
    public UserClientActivityDto getById(Long id) {
        return mapper.toDto(resolveEntity(id, currentUserProvider.getUsername(), currentUserProvider.isAdmin()));
    }

    @Override
    @Transactional
    public UserClientActivityDto update(Long id, UserClientActivityDto dto) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        UserClientActivity existing = resolveEntity(id, username, isAdmin);
        Client client = resolveClient(dto.getClientId(), username, isAdmin);
        applyFeeCalculation(dto, client, username, isAdmin);
        mapper.updateEntityFromDto(dto, existing);
        UserClientActivity saved = repository.save(existing);
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
    public ResponseList<UserClientActivityDto> findAll(UserClientActivityDto filter, Pageable pageable) {
        String username = currentUserProvider.getUsername();
        boolean isAdmin = currentUserProvider.isAdmin();
        Pageable effectivePageable = pageable != null ? pageable : Pageable.unpaged();
        boolean hasClientFilter = filter != null && filter.getClientId() != null;

        Page<UserClientActivity> page;
        if (isAdmin) {
            page = hasClientFilter
                    ? repository.findAllByClient_Id(filter.getClientId(), effectivePageable)
                    : repository.findAll(effectivePageable);
        } else {
            page = hasClientFilter
                    ? repository.findAllByClient_IdAndClient_Owner_Username(filter.getClientId(), username, effectivePageable)
                    : repository.findAllByClient_Owner_Username(username, effectivePageable);
        }

        List<UserClientActivityDto> data = mapper.toDtoList(page.getContent());

        ResponsePageable<UserClientActivityDto> response = new ResponsePageable<>();
        response.setData(data);
        response.setTotalItems(page.getTotalElements());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    public List<UserClientActivityDto> findAllForMonth(Long month) {
        List<UserClientActivity> activities = currentUserProvider.isAdmin()
                ? repository.findAllByMonth(month)
                : repository.findAllByAppUserAndMonth(currentUserProvider.getUsername(), month);
        return mapper.toDtoList(activities);
    }

    private Client resolveClient(Long clientId, String username, boolean isAdmin) {
        return isAdmin
                ? clientRepository.findById(clientId)
                        .orElseThrow(() -> new EntityNotFoundException("Client with id: " + clientId + " not found"))
                : clientRepository.findByIdAndOwner_Username(clientId, username)
                        .orElseThrow(() -> new EntityNotFoundException("Client with id: " + clientId + " not found"));
    }

    private UserClientActivity resolveEntity(Long id, String username, boolean isAdmin) {
        return isAdmin
                ? repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"))
                : repository.findByIdAndClient_Owner_Username(id, username)
                        .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"));
    }

    private void applyFeeCalculation(UserClientActivityDto dto, Client client, String username, boolean isAdmin) {
        FeeType type = client.getType();
        if (type == null) {
            throw new InvalidActivityException("Configurare prima la tariffa del cliente.");
        }

        if (type == FeeType.DAILY) {
            if (client.getFee() == null) {
                throw new InvalidActivityException("Il cliente non ha una tariffa giornaliera configurata.");
            }
            if (dto.getQuantity() == null || dto.getQuantity() < 1) {
                throw new InvalidActivityException("Per un cliente a tariffa giornaliera è obbligatorio indicare la quantità (giorni).");
            }
            dto.setServices(List.of());
            dto.setPrice(null);
            dto.setFee(client.getFee().multiply(BigDecimal.valueOf(dto.getQuantity())));
            return;
        }

        if (client.getFee() == null) {
            throw new InvalidActivityException("Il cliente non ha una percentuale configurata.");
        }
        List<ServiceSelectionDto> selections = dto.getServices();
        if (selections == null || selections.isEmpty()) {
            throw new InvalidActivityException("Per un cliente a percentuale è obbligatorio selezionare almeno un servizio svolto.");
        }

        List<Long> serviceIds = selections.stream().map(ServiceSelectionDto::getServiceId).toList();
        if (serviceIds.size() != new HashSet<>(serviceIds).size()) {
            throw new InvalidActivityException("Non è possibile selezionare lo stesso servizio più volte.");
        }

        List<ClientBillableService> services = isAdmin
                ? clientBillableServiceRepository.findAllById(serviceIds)
                : clientBillableServiceRepository.findAllByIdInAndClient_Owner_Username(serviceIds, username);
        boolean allOwnedByThisClient = services.size() == serviceIds.size()
                && services.stream().allMatch(service -> service.getClient().getId().equals(client.getId()));
        if (!allOwnedByThisClient) {
            throw new InvalidActivityException("Uno o più servizi selezionati non appartengono al cliente indicato.");
        }

        Map<Long, BigDecimal> priceByServiceId = services.stream()
                .collect(Collectors.toMap(ClientBillableService::getId, ClientBillableService::getPrice));

        BigDecimal totalPrice = selections.stream()
                .map(selection -> priceByServiceId.get(selection.getServiceId())
                        .multiply(BigDecimal.valueOf(selection.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal calculatedFee = totalPrice
                .multiply(client.getFee())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        dto.setQuantity(null);
        dto.setPrice(totalPrice);
        dto.setFee(calculatedFee);
    }
}
