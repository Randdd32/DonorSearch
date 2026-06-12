package com.github.randdd32.donor_search_backend.service.search;

import com.github.randdd32.donor_search_backend.core.error.PdfGenerationException;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {
    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private PdfExportService pdfExportService;

    @Test
    @DisplayName("Позитивный тест: успешная генерация байтового массива PDF")
    void generateDonorReport_Positive() {
        List<DonorResultDto> results = Collections.emptyList();

        when(templateEngine.process(eq("donor-report"), any(Context.class)))
                .thenReturn("<html><body><h1>Test Report</h1></body></html>");

        byte[] pdfBytes = pdfExportService.generateDonorReport(results);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Негативный тест: ошибка парсинга шаблона Thymeleaf (выброс PdfGenerationException)")
    void generateDonorReport_Negative_TemplateError() {
        List<DonorResultDto> results = Collections.emptyList();

        when(templateEngine.process(eq("donor-report"), any(Context.class)))
                .thenThrow(new RuntimeException("Thymeleaf parsing error"));

        PdfGenerationException ex = assertThrows(PdfGenerationException.class, () ->
                pdfExportService.generateDonorReport(results)
        );

        assertTrue(ex.getMessage().contains("Error during PDF document generation"));
        assertEquals("Thymeleaf parsing error", ex.getCause().getMessage());
    }
}
