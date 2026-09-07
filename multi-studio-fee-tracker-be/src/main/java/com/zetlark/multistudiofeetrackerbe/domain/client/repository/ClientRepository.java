package com.zetlark.multistudiofeetrackerbe.domain.client.repository;

import com.zetlark.multistudiofeetrackerbe.domain.client.entity.Client;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByIdAndOwner_Username(Long id, String username);

    boolean existsByIdAndOwner_Username(Long id, String username);

    Page<Client> findAllByOwner_Username(String username, Pageable pageable);

    Page<Client> findAllByOwner_UsernameAndNameContainingIgnoreCase(String username, String name, Pageable pageable);

    Page<Client> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
