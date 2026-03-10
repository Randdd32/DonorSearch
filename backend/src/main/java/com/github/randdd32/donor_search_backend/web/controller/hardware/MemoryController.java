package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import com.github.randdd32.donor_search_backend.service.hardware.MemoryService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.MemoryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.MemoryMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/ram")
public class MemoryController extends AbstractReadController<MemoryEntity, MemoryDto, MemoryService> {
    public MemoryController(MemoryService service, MemoryMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<MemoryDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> formFactorIds,
            @RequestParam(required = false) List<Long> memoryTypeIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) Integer minFrequency,
            @RequestParam(required = false) Integer maxFrequency,
            @RequestParam(required = false) Integer minModulesCount,
            @RequestParam(required = false) Integer maxModulesCount,
            @RequestParam(required = false) Integer minModulesSize,
            @RequestParam(required = false) Integer maxModulesSize,
            @RequestParam(required = false) Integer minCas,
            @RequestParam(required = false) Integer maxCas,
            @RequestParam(required = false) Boolean isEcc,
            @RequestParam(required = false) Boolean isRegistered,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, formFactorIds, memoryTypeIds, colorIds,
                        minFrequency, maxFrequency, minModulesCount, maxModulesCount,
                        minModulesSize, maxModulesSize, minCas, maxCas, isEcc, isRegistered, pageable),
                toDtoMapper
        );
    }
}
