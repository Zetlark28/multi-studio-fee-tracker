package com.zetlark.multistudiofeetrackerbe.domain.client.dto;

import java.math.BigDecimal;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import com.zetlark.multistudiofeetrackerbe.domain.client.entity.FeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto extends BaseDto {

    private Long id;

    private String name;

    private BigDecimal fee;

    private FeeType type;
}
