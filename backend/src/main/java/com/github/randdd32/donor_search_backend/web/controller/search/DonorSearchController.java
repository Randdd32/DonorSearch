package com.github.randdd32.donor_search_backend.web.controller.search;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.search.DonorSearchService;
import com.github.randdd32.donor_search_backend.service.search.PdfExportService;
import com.github.randdd32.donor_search_backend.web.dto.filter.DonorSearchFilter;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(Constants.API_URL + "/search")
@RequiredArgsConstructor
public class DonorSearchController {
    private final DonorSearchService searchService;
    private final PdfExportService pdfExportService;

    @PostMapping("/run")
    public Map<String, String> runSearch(
            @RequestParam Long targetDeviceId,
            @RequestParam(required = false) Long targetAdapterId,
            @RequestParam(required = false) ExternalComponentCategory category) {
        String sessionId = searchService.runSearch(targetDeviceId, targetAdapterId, category);
        return Map.of("sessionId", sessionId);
    }

    @GetMapping("/results/{sessionId}")
    public PageDto<DonorResultDto> getResults(
            @PathVariable String sessionId,
            @ModelAttribute DonorSearchFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "totalPenalty") Pageable pageable) {

        return searchService.getResults(sessionId, filter, pageable);
    }

    @GetMapping(value = "/results/{sessionId}/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable String sessionId,
            @ModelAttribute DonorSearchFilter filter) {
        PageDto<DonorResultDto> resultsPage = searchService.getResults(
                sessionId,
                filter,
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by("totalPenalty").ascending())
        );

        byte[] pdfBytes = pdfExportService.generateDonorReport(resultsPage.items());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "donor-report.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
