package com.zetlark.multistudiofeetrackerbe.domain.activity.repository;

import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserClientActivityRepository extends JpaRepository<UserClientActivity, Long> {

    Optional<UserClientActivity> findByIdAndClient_Owner_Username(Long id, String username);

    boolean existsByIdAndClient_Owner_Username(Long id, String username);

    Page<UserClientActivity> findAllByClient_Owner_Username(String username, Pageable pageable);

    Page<UserClientActivity> findAllByClient_IdAndClient_Owner_Username(
            Long clientId, String username, Pageable pageable);

    Page<UserClientActivity> findAllByClient_Id(Long clientId, Pageable pageable);

    @Query("SELECT uca FROM UserClientActivity uca WHERE uca.client.owner.username = :appUserUsername AND MOD(uca.date, 1000000) = :month")
    List<UserClientActivity> findAllByAppUserAndMonth(String appUserUsername, Long month);

    @Query("SELECT uca FROM UserClientActivity uca WHERE MOD(uca.date, 1000000) = :month")
    List<UserClientActivity> findAllByMonth(Long month);
}
