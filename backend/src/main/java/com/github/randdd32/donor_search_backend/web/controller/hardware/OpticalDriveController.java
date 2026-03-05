package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.hardware.OpticalDriveService;
import com.github.randdd32.donor_search_backend.web.dto.hardware.OpticalDriveDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.OpticalDriveMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/optical-drives")
@RequiredArgsConstructor
public class OpticalDriveController {
    private final OpticalDriveService service;
    private final OpticalDriveMapper mapper;

    @GetMapping
    public PageDto<OpticalDriveDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> formFactorIds,
            @RequestParam(required = false) List<Long> interfaceIds,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, formFactorIds, interfaceIds, pageable),
                mapper::toDto
        );
    }

    @GetMapping("/{id}")
    public OpticalDriveDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }
}
