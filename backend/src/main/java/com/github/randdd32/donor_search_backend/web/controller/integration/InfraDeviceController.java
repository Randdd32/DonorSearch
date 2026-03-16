package com.github.randdd32.donor_search_backend.web.controller.integration;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.INFRA_URL + "/devices")
@RequiredArgsConstructor
public class InfraDeviceController {
    private final InfraDeviceService service;

    @GetMapping
    public PageDto<ExternalDeviceDto> getDevices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> stateIds,
            @RequestParam(required = false) List<Long> departmentIds,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false) List<Long> modelIds,
            @RequestParam(required = false) List<Long> buildingIds,
            @RequestParam(required = false) List<Long> floorIds,
            @RequestParam(required = false) List<Long> roomIds,
            @RequestParam(required = false) Instant dateReceivedFrom,
            @RequestParam(required = false) Instant dateReceivedTo,
            @RequestParam(required = false) Boolean isWorking,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return service.getDevicesPage(
                search, stateIds, departmentIds, manufacturerIds, typeIds, modelIds,
                buildingIds, floorIds, roomIds,
                dateReceivedFrom, dateReceivedTo, isWorking, pageable
        );
    }

    @GetMapping("/{id}")
    public ExternalDeviceDto getDeviceDetails(@PathVariable Long id) {
        return service.getDeviceDetails(id);
    }
}
