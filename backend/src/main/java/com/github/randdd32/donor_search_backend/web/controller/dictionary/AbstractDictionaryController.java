package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.dictionary.AbstractDictionaryService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.NamedDictionaryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractDictionaryController<E, S extends AbstractDictionaryService<E, ?>>
        extends AbstractReadController<E, NamedDictionaryDto, S> {
    protected AbstractDictionaryController(S service, Function<E, NamedDictionaryDto> toDtoMapper) {
        super(service, toDtoMapper);
    }

    @GetMapping
    public PageDto<NamedDictionaryDto> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "name") Pageable pageable) {
        return PageDtoMapper.toDto(service.getAll(search, pageable), toDtoMapper);
    }

    @GetMapping("/ids")
    public List<NamedDictionaryDto> getByIds(@RequestParam(required = false) List<Long> ids) {
        return service.getByIds(ids).stream().map(toDtoMapper).toList();
    }
}
