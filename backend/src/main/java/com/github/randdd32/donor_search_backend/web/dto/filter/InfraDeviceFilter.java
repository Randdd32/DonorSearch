package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InfraDeviceFilter(
        String search,
        List<String> stateIds,
        List<String> departmentIds,
        List<String> typeIds,
        List<Long> manufacturerIds,
        List<Long> modelIds,
        List<Long> buildingIds,
        List<Long> floorIds,
        List<Long> roomIds,
        Instant dateReceivedFrom,
        Instant dateReceivedTo,
        Instant dateInquiryFrom,
        Instant dateInquiryTo,
        Instant appointmentDateFrom,
        Instant appointmentDateTo,
        BigDecimal minCost,
        BigDecimal maxCost,
        Boolean isWorking
) {}
