package com.github.randdd32.donor_search_backend.web.controller;

import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.function.Function;

public abstract class AbstractReadController<E, D, S extends AbstractReadService<E, ?>> {
    protected final S service;
    protected final Function<E, D> toDtoMapper;

    protected AbstractReadController(S service, Function<E, D> toDtoMapper) {
        this.service = service;
        this.toDtoMapper = toDtoMapper;
    }

    @GetMapping("/{id}")
    public D getById(@PathVariable("id") Long id) {
        return toDtoMapper.apply(service.getById(id));
    }
}
