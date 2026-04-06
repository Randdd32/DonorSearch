package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.service.hardware.CaseService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.filter.CaseFilter;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CaseDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.CaseMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/cases")
public class CaseController extends AbstractReadController<CaseEntity, CaseDto, CaseService> {
    public CaseController(CaseService service, CaseMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<CaseDto> getAll(
            @ModelAttribute CaseFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(service.getAll(filter, pageable), toDtoMapper);
    }
}
