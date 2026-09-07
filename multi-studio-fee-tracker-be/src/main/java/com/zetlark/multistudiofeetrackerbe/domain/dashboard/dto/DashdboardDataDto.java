package com.zetlark.multistudiofeetrackerbe.domain.dashboard.dto;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DashdboardDataDto extends BaseDto {

    private BigDecimal totalRevenue;
    private Long workDays;
    private Long month;
    private Long totalClients;
}
