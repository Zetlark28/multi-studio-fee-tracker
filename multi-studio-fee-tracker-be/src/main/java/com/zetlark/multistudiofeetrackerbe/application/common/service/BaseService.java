package com.zetlark.multistudiofeetrackerbe.application.common.service;


import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import org.springframework.data.domain.Pageable;

public interface BaseService<DTO extends BaseDto, ID> {


    DTO create(DTO dto);

    DTO getById(ID id);

    DTO update(ID id, DTO dto);

//    List<DTO> findAll(DTO filter);

    ResponseList<DTO> findAll(DTO filter, Pageable pageable);

    void delete(ID id);

    boolean existsById(ID id);
}