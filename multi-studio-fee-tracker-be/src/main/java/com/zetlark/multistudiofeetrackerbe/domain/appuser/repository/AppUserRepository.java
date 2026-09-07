package com.zetlark.multistudiofeetrackerbe.domain.appuser.repository;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
}
