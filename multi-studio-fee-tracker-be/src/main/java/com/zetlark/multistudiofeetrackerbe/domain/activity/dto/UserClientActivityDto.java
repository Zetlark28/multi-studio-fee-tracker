package com.zetlark.multistudiofeetrackerbe.domain.activity.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserClientActivityDto extends BaseDto {

    private Long id;

    @NotNull
    private Long clientId;

    @Valid
    private List<ServiceSelectionDto> services;

    @NotNull
    private Long date;

    /** Obbligatoria solo per clienti a tariffa DAILY (numero di giorni); ignorata per PERCENT. */
    private Integer quantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal price;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal fee;
}
