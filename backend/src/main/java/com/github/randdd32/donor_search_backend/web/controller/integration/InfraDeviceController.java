package com.github.randdd32.donor_search_backend.web.controller.integration;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.log.NoLogging;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.web.dto.filter.InfraDeviceFilter;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.INFRA_URL + "/devices")
@RequiredArgsConstructor
public class InfraDeviceController {
    private final InfraDeviceService service;

    @NoLogging
    @GetMapping
    public PageDto<ExternalDeviceDto> getDevices(
            @ModelAttribute InfraDeviceFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return service.getDevicesPage(filter, pageable);
    }

    @GetMapping("/{id}")
    public ExternalDeviceDto getDeviceDetails(@PathVariable Long id) {
        return service.getDeviceDetails(id);
    }
}
