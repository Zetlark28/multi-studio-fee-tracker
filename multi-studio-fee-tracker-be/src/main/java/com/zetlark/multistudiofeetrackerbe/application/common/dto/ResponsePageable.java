package com.zetlark.multistudiofeetrackerbe.application.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsePageable<DTO extends BaseDto> extends ResponseList<DTO> {

    private int page;
    private int size;
}
