package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.service.hardware.MotherboardService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.MotherboardDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.MotherboardMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/motherboards")
public class MotherboardController extends AbstractReadController<MotherboardEntity, MotherboardDto, MotherboardService> {
    public MotherboardController(MotherboardService service, MotherboardMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<MotherboardDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> socketIds,
            @RequestParam(required = false) List<Long> formFactorIds,
            @RequestParam(required = false) List<Long> memoryTypeIds,
            @RequestParam(required = false) Integer minMaxMemoryGb,
            @RequestParam(required = false) Integer minMemorySlots,
            @RequestParam(required = false) Integer minMemorySpeedMhz,
            @RequestParam(required = false) Boolean eccSupport,
            @RequestParam(required = false) Boolean usesBackConnect,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, socketIds, formFactorIds, memoryTypeIds,
                        minMaxMemoryGb, minMemorySlots, minMemorySpeedMhz, eccSupport, usesBackConnect, pageable),
                toDtoMapper
        );
    }
}
