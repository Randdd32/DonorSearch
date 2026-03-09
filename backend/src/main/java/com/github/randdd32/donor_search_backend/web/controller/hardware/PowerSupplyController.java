package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.service.hardware.PowerSupplyService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.PowerSupplyDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.PowerSupplyMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/power-supplies")
public class PowerSupplyController extends AbstractReadController<PowerSupplyEntity, PowerSupplyDto, PowerSupplyService> {
    public PowerSupplyController(PowerSupplyService service, PowerSupplyMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<PowerSupplyDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false) List<Long> efficiencyIds,
            @RequestParam(required = false) List<Long> modularIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) Integer minWattage,
            @RequestParam(required = false) Integer maxWattage,
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, typeIds, efficiencyIds, modularIds, colorIds,
                        minWattage, maxWattage, minLength, maxLength, pageable),
                toDtoMapper
        );
    }
}
