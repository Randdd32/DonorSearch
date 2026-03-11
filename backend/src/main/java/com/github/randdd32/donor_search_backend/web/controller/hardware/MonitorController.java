package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.service.hardware.MonitorService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.MonitorDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.MonitorMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/monitors")
public class MonitorController extends AbstractReadController<MonitorEntity, MonitorDto, MonitorService> {
    public MonitorController(MonitorService service, MonitorMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<MonitorDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> resolutionIds,
            @RequestParam(required = false) List<Long> panelTypeIds,
            @RequestParam(required = false) List<Long> aspectRatioIds,
            @RequestParam(required = false) Double minScreenSize,
            @RequestParam(required = false) Double maxScreenSize,
            @RequestParam(required = false) Integer minRefreshRate,
            @RequestParam(required = false) Integer maxRefreshRate,
            @RequestParam(required = false) Double minResponseTime,
            @RequestParam(required = false) Double maxResponseTime,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, resolutionIds, panelTypeIds, aspectRatioIds,
                        minScreenSize, maxScreenSize, minRefreshRate, maxRefreshRate,
                        minResponseTime, maxResponseTime, pageable),
                toDtoMapper
        );
    }
}
