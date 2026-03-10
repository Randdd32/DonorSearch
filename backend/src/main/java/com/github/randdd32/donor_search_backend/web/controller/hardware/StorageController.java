package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.service.hardware.StorageService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.StorageDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.StorageMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/storages")
public class StorageController extends AbstractReadController<StorageEntity, StorageDto, StorageService> {
    public StorageController(StorageService service, StorageMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<StorageDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false) List<Long> formFactorIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) List<Long> interfaceIds,
            @RequestParam(required = false) Boolean isExternal,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) Integer minCache,
            @RequestParam(required = false) Integer maxCache,
            @RequestParam(required = false) Integer minRpm,
            @RequestParam(required = false) Integer maxRpm,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, typeIds, formFactorIds, colorIds, interfaceIds,
                        isExternal, minCapacity, maxCapacity, minCache, maxCache, minRpm, maxRpm, pageable),
                toDtoMapper
        );
    }
}
