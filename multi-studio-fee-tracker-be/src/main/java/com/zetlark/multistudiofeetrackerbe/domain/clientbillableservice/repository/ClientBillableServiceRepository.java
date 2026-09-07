package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.repository;

import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.entity.ClientBillableService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientBillableServiceRepository extends JpaRepository<ClientBillableService, Long> {

    boolean existsByClientId(Long clientId);

    List<ClientBillableService> findAllByIdInAndClient_Owner_Username(List<Long> ids, String username);

    boolean existsByClientIdAndNameIgnoreCase(Long clientId, String name);

    boolean existsByClientIdAndNameIgnoreCaseAndIdNot(Long clientId, String name, Long id);

    Optional<ClientBillableService> findByIdAndClient_Owner_Username(Long id, String username);

    boolean existsByIdAndClient_Owner_Username(Long id, String username);

    Page<ClientBillableService> findAllByClient_IdAndClient_Owner_Username(
            Long clientId, String username, Pageable pageable);

    Page<ClientBillableService> findAllByClient_Owner_Username(String username, Pageable pageable);

    Page<ClientBillableService> findAllByClient_Id(Long clientId, Pageable pageable);
}
