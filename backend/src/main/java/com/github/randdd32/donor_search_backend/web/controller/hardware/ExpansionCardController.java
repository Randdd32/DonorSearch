package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.log.NoLogging;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.service.hardware.ExpansionCardService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.filter.ExpansionCardFilter;
import com.github.randdd32.donor_search_backend.web.dto.hardware.ExpansionCardDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.ExpansionCardMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/expansion-cards")
public class ExpansionCardController extends AbstractReadController<ExpansionCardEntity, ExpansionCardDto, ExpansionCardService> {
    public ExpansionCardController(ExpansionCardService service, ExpansionCardMapper mapper) {
        super(service, mapper::toDto);
    }

    @NoLogging
    @GetMapping
    public PageDto<ExpansionCardDto> getAll(
            @ModelAttribute ExpansionCardFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(service.getAll(filter, pageable), toDtoMapper);
    }
}
