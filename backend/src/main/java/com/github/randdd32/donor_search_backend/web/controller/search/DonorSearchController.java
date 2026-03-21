package com.github.randdd32.donor_search_backend.web.controller.search;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.search.DonorSearchService;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.API_URL + "/search")
@RequiredArgsConstructor
public class DonorSearchController {
    private final DonorSearchService searchService;

    @PostMapping("/run")
    public Map<String, String> runSearch(@RequestParam Long targetDeviceId, @RequestParam Long targetAdapterId) {
        String sessionId = searchService.runSearch(targetDeviceId, targetAdapterId);
        return Map.of("sessionId", sessionId);
    }

    @GetMapping("/results/{sessionId}")
    public PageDto<DonorResultDto> getResults(
            @PathVariable String sessionId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> stateIds,
            @RequestParam(required = false) List<Long> departmentIds,
            @RequestParam(required = false) List<Long> buildingIds,
            @RequestParam(required = false) List<Long> floorIds,
            @RequestParam(required = false) List<Long> roomIds,
            @RequestParam(required = false) List<Long> deviceManufacturerIds,
            @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false) List<Long> modelIds,
            @RequestParam(required = false) Instant dateReceivedFrom,
            @RequestParam(required = false) Instant dateReceivedTo,
            @RequestParam(required = false) Boolean isWorking,
            @RequestParam(required = false) List<Long> componentManufacturerIds,
            @RequestParam(required = false) Integer maxTotalPenalty,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "totalPenalty") Pageable pageable) {

        return searchService.getResults(
                sessionId, search, stateIds, departmentIds, buildingIds, floorIds, roomIds,
                deviceManufacturerIds, typeIds, modelIds, dateReceivedFrom, dateReceivedTo,
                isWorking, componentManufacturerIds, maxTotalPenalty, pageable
        );
    }
}
