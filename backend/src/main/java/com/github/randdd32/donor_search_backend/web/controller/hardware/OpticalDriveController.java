package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.hardware.OpticalDriveService;
import com.github.randdd32.donor_search_backend.web.dto.hardware.OpticalDriveDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.OpticalDriveMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + "/components/optical-drives")
@RequiredArgsConstructor
public class OpticalDriveController {
    private final OpticalDriveService service;
    private final OpticalDriveMapper mapper;

    @GetMapping
    public PageDto<OpticalDriveDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return PageDtoMapper.toDto(
                service.getAll(search, page, size),
                mapper::toDto
        );
    }

    @GetMapping("/{id}")
    public OpticalDriveDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }
}
