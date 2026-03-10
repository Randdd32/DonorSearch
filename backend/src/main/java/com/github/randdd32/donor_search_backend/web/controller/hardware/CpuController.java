package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.service.hardware.CpuService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CpuDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.CpuMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/cpus")
public class CpuController extends AbstractReadController<CpuEntity, CpuDto, CpuService> {
    public CpuController(CpuService service, CpuMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<CpuDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> socketIds,
            @RequestParam(required = false) List<Long> microarchitectureIds,
            @RequestParam(required = false) List<Long> graphicsIds,
            @RequestParam(required = false) Integer minCoreCount,
            @RequestParam(required = false) Integer maxCoreCount,
            @RequestParam(required = false) Double minCoreClock,
            @RequestParam(required = false) Double maxCoreClock,
            @RequestParam(required = false) Double minBoostClock,
            @RequestParam(required = false) Double maxBoostClock,
            @RequestParam(required = false) Integer minTdp,
            @RequestParam(required = false) Integer maxTdp,
            @RequestParam(required = false) Boolean eccSupport,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(
                        search, manufacturerIds, socketIds, microarchitectureIds, graphicsIds,
                        minCoreCount, maxCoreCount, minCoreClock, maxCoreClock,
                        minBoostClock, maxBoostClock, minTdp, maxTdp, eccSupport, pageable),
                toDtoMapper
        );
    }
}
