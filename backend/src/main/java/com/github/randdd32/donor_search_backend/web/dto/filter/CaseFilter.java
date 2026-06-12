package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record CaseFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> caseTypeIds,
        List<Long> colorIds,
        List<Long> sidePanelIds,
        Integer minLength,
        Integer maxLength,
        Integer minWidth,
        Integer maxWidth,
        Integer minHeight,
        Integer maxHeight,
        Integer minInt35Bays,
        Integer minExpansionSlots,
        List<Long> moboFormFactorIds,
        List<Long> frontPanelUsbIds
) {}
