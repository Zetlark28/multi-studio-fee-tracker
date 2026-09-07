package com.zetlark.multistudiofeetrackerbe.domain.client.entity;

import java.math.BigDecimal;

import com.zetlark.multistudiofeetrackerbe.application.common.entity.BaseEntity;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUser;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_username", nullable = false, updatable = false)
    private AppUser owner;

    private String name;

    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    private FeeType type;
}
