package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.time.Instant;
import java.util.List;

public record InfraDeviceFilter(
        String search,
        List<Long> stateIds,
        List<Long> departmentIds,
        List<Long> manufacturerIds,
        List<Long> typeIds,
        List<Long> modelIds,
        List<Long> buildingIds,
        List<Long> floorIds,
        List<Long> roomIds,
        Instant dateReceivedFrom,
        Instant dateReceivedTo,
        Boolean isWorking
) {}
