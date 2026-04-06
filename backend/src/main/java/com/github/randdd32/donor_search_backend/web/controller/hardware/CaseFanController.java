package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.service.hardware.CaseFanService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.filter.CaseFanFilter;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CaseFanDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.CaseFanMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/case-fans")
public class CaseFanController extends AbstractReadController<CaseFanEntity, CaseFanDto, CaseFanService> {
    public CaseFanController(CaseFanService service, CaseFanMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<CaseFanDto> getAll(
            @ModelAttribute CaseFanFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(service.getAll(filter, pageable), toDtoMapper);
    }
}
