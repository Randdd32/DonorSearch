package com.github.randdd32.donor_search_backend.web.controller.compatibility;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.service.compatibility.CompatibilityRuleService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractCrudController;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.CompatibilityRuleDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.compatibility.CompatibilityRuleMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + "/compatibility-rules")
public class CompatibilityRuleController extends AbstractCrudController<CompatibilityRuleEntity, CompatibilityRuleDto,
        CompatibilityRuleService> {
    public CompatibilityRuleController(CompatibilityRuleService service, CompatibilityRuleMapper mapper) {
        super(service, mapper::toDto, mapper::toEntity);
    }

    @GetMapping
    public PageDto<CompatibilityRuleDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) List<ComponentType> targetTypes,
            @RequestParam(required = false) Instant createdAfter,
            @RequestParam(required = false) Instant createdBefore,
            @RequestParam(required = false) Instant updatedAfter,
            @RequestParam(required = false) Instant updatedBefore,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, isActive, targetTypes,
                        createdAfter, createdBefore, updatedAfter, updatedBefore, pageable),
                toDtoMapper
        );
    }
}
