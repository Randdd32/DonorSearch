package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.service.hardware.CpuCoolerService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CpuCoolerDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.CpuCoolerMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/cpu-coolers")
public class CpuCoolerController extends AbstractReadController<CpuCoolerEntity, CpuCoolerDto, CpuCoolerService> {
    public CpuCoolerController(CpuCoolerService service, CpuCoolerMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<CpuCoolerDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) List<Long> socketIds,
            @RequestParam(required = false) Boolean isWaterCooled,
            @RequestParam(required = false) Integer minHeight,
            @RequestParam(required = false) Integer maxHeight,
            @RequestParam(required = false) Integer minWaterSize,
            @RequestParam(required = false) Integer maxWaterSize,
            @RequestParam(required = false) Integer minRpm,
            @RequestParam(required = false) Integer maxRpm,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, colorIds, socketIds, isWaterCooled,
                        minHeight, maxHeight, minWaterSize, maxWaterSize, minRpm, maxRpm, pageable),
                toDtoMapper
        );
    }
}
