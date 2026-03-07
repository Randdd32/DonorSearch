package com.github.randdd32.donor_search_backend.web.controller;

import com.github.randdd32.donor_search_backend.service.AbstractCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Function;

public abstract class AbstractCrudController<E, D, S extends AbstractCrudService<E, ?>>
        extends AbstractReadController<E, D, S> {
    protected final Function<D, E> toEntityMapper;

    protected AbstractCrudController(S service,
                                     Function<E, D> toDtoMapper,
                                     Function<D, E> toEntityMapper) {
        super(service, toDtoMapper);
        this.toEntityMapper = toEntityMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public D create(@RequestBody @Valid D dto) {
        E createdEntity = service.create(toEntityMapper.apply(dto));
        return toDtoMapper.apply(createdEntity);
    }

    @PutMapping("/{id}")
    public D update(@PathVariable("id") Long id, @RequestBody @Valid D dto) {
        E updatedEntity = service.update(id, toEntityMapper.apply(dto));
        return toDtoMapper.apply(updatedEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
