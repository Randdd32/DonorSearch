package com.github.randdd32.donor_search_backend.web.controller;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.web.dto.IntegrationMappingDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.IntegrationMappingMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping(Constants.API_URL + "/mappings")
public class IntegrationMappingController extends AbstractCrudController<IntegrationMappingEntity, IntegrationMappingDto,
        IntegrationMappingService> {
    public IntegrationMappingController(IntegrationMappingService service, IntegrationMappingMapper mapper) {
        super(service, mapper::toDto, mapper::toEntity);
    }

    @GetMapping
    public PageDto<IntegrationMappingDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MappingConfidence confidence,
            @RequestParam(required = false) ComponentType componentType,
            @RequestParam(required = false) Instant createdAfter,
            @RequestParam(required = false) Instant createdBefore,
            @RequestParam(required = false) Instant updatedAfter,
            @RequestParam(required = false) Instant updatedBefore,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, confidence, componentType, createdAfter, createdBefore, updatedAfter, updatedBefore, pageable),
                toDtoMapper
        );
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntegrationMappingDto create(@RequestBody @Valid IntegrationMappingDto dto) {
        return toDtoMapper.apply(service.createFromDto(toEntityMapper.apply(dto), dto.internalComponentId()));
    }

    @Override
    @PutMapping("/{id}")
    public IntegrationMappingDto update(@PathVariable("id") Long id, @RequestBody @Valid IntegrationMappingDto dto) {
        return toDtoMapper.apply(service.updateInternalComponent(id, dto.internalComponentId(), dto.confidence()));
    }
}
