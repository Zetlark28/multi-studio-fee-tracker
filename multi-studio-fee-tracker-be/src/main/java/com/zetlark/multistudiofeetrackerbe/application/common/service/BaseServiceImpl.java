package com.zetlark.multistudiofeetrackerbe.application.common.service;



import com.zetlark.multistudiofeetrackerbe.application.common.dto.BaseDto;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponsePageable;
import com.zetlark.multistudiofeetrackerbe.application.common.entity.BaseEntity;
import com.zetlark.multistudiofeetrackerbe.application.common.mapper.BaseMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class BaseServiceImpl<E extends BaseEntity, D extends BaseDto, I> implements BaseService<D, I> {

    protected final JpaRepository<E, I> repository;
    protected final BaseMapper<E, D> mapper;

    protected BaseServiceImpl(JpaRepository<E, I> repository, BaseMapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public D create(D dto) {
        E entity = mapper.toEntity(dto);
        E savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

    @Override
    public D getById(I id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public D update(I id, D dto) {
        E existingEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id: " + id + " not found"));
        mapper.updateEntityFromDto(dto, existingEntity);
        E savedEntity = repository.save(existingEntity);
        return mapper.toDto(savedEntity);
    }


    @Override
    public ResponseList<D> findAll(D filter, Pageable pageable) {

        if (pageable == null) {
            ResponseList<D> response = new ResponseList<>();
            response.setData(findAll(filter));
            response.setTotalItems(response.getData().size());
            return response;
        }
        Page<D> page = pageableFindAll(filter, pageable);
        ResponsePageable<D> response = new ResponsePageable<>();
        response.setData(page.getContent());
        response.setTotalItems(page.getTotalElements());
        response.setTotalItems(page.getTotalElements());
        response.setSize(page.getSize());
        response.setPage(page.getNumber());
        return response;
    }

    private List<D> findAll(D filter) {
        List<E> entities;
        if (filter != null) {
            E filterEntity = mapper.toEntity(filter);
            ExampleMatcher filterMatcher = ExampleMatcher.matchingAny().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            entities = repository.findAll(Example.of(filterEntity, filterMatcher));
        } else {
            entities = repository.findAll();
        }
        return mapper.toDtoList(entities);
    }

    private Page<D> pageableFindAll(D filter, Pageable pageable) {
        Page<E> entities;
        if (filter != null) {
            E filterEntity = mapper.toEntity(filter);
            ExampleMatcher filterMatcher = ExampleMatcher.matchingAny().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            entities = repository.findAll(Example.of(filterEntity, filterMatcher), pageable);
        } else {
            entities = repository.findAll(Pageable.unpaged());
        }
        List<D> dtoList = mapper.toDtoList(entities.getContent());
        return new PageImpl<>(dtoList, pageable, entities.getTotalElements());
    }


    @Override
    @Transactional
    public void delete(I id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Entity with id: " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(I id) {
        return repository.existsById(id);
    }


}