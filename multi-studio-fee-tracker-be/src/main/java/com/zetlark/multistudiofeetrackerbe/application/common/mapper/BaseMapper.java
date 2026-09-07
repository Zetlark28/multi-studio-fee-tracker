package com.zetlark.multistudiofeetrackerbe.application.common.mapper;




import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import com.zetlark.multistudiofeetrackerbe.application.common.entity.BaseEntity;

import java.util.List;

public interface BaseMapper<E extends BaseEntity, DTO extends BaseDto> {
    DTO toDto(E entity);

    E toEntity(DTO dto);

    List<DTO> toDtoList(List<E> entities);

    List<E> toEntityList(List<DTO> dtos);

    void updateEntityFromDto(DTO dto, E entity);

}