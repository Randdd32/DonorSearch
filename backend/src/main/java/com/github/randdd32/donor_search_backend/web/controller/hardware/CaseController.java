package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.service.hardware.CaseService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CaseDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.CaseMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/cases")
public class CaseController extends AbstractReadController<CaseEntity, CaseDto, CaseService> {
    public CaseController(CaseService service, CaseMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<CaseDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> caseTypeIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) List<Long> sidePanelIds,
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) Integer minWidth,
            @RequestParam(required = false) Integer maxWidth,
            @RequestParam(required = false) Integer minHeight,
            @RequestParam(required = false) Integer maxHeight,
            @RequestParam(required = false) Integer minInt35Bays,
            @RequestParam(required = false) Integer minExpansionSlots,
            @RequestParam(required = false) List<Long> moboFormFactorIds,
            @RequestParam(required = false) List<Long> frontPanelUsbIds,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(
                        search, manufacturerIds, caseTypeIds, colorIds, sidePanelIds,
                        minLength, maxLength, minWidth, maxWidth, minHeight, maxHeight,
                        minInt35Bays, minExpansionSlots, moboFormFactorIds, frontPanelUsbIds, pageable),
                toDtoMapper
        );
    }
}
