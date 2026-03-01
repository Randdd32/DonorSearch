package com.github.randdd32.donor_search_backend.web.controller;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.CompatibilityRuleService;
import com.github.randdd32.donor_search_backend.web.dto.CompatibilityRuleDto;
import com.github.randdd32.donor_search_backend.web.mapper.CompatibilityRuleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + "/compatibility-rules")
@RequiredArgsConstructor
public class CompatibilityRuleController {
    private final CompatibilityRuleService service;
    private final CompatibilityRuleMapper mapper;

    @GetMapping
    public Page<CompatibilityRuleDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return service.getAll(search, page, size).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public CompatibilityRuleDto get(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompatibilityRuleDto create(@RequestBody @Valid CompatibilityRuleDto dto) {
        return mapper.toDto(service.create(mapper.toEntity(dto)));
    }

    @PutMapping("/{id}")
    public CompatibilityRuleDto update(@PathVariable Long id, @RequestBody @Valid CompatibilityRuleDto dto) {
        return mapper.toDto(service.update(id, mapper.toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
