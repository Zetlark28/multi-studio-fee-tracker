package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto;

import java.math.BigDecimal;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientBillableServiceDto extends BaseDto {

    private Long id;

    private Long clientId;

    @NotBlank
    private String name;

    private BigDecimal price;
}
