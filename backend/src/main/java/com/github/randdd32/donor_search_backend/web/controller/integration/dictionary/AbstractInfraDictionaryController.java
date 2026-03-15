package com.github.randdd32.donor_search_backend.web.controller.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.integration.dictionary.AbstractInfraDictionaryService;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.NamedDictionaryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public abstract class AbstractInfraDictionaryController {
    protected final AbstractInfraDictionaryService service;

    protected AbstractInfraDictionaryController(AbstractInfraDictionaryService service) {
        this.service = service;
    }

    @GetMapping
    public PageDto<NamedDictionaryDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> parentIds,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.search(search, parentIds, pageable);
    }

    @GetMapping("/ids")
    public List<NamedDictionaryDto> getByIds(@RequestParam(required = false) List<Long> ids) {
        return service.getByIds(ids);
    }
}
