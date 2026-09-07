package com.zetlark.multistudiofeetrackerbe.application.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseList <DTO extends BaseDto>{

    private List<DTO> data;
    private long totalItems;
}