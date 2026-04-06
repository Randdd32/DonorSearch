package com.github.randdd32.donor_search_backend.service.search;

import com.github.randdd32.donor_search_backend.core.error.PdfGenerationException;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {
    private final TemplateEngine templateEngine;

    public byte[] generateDonorReport(List<DonorResultDto> results) {
        try {
            Context context = new Context();
            context.setVariable("results", results);

            String htmlContent = templateEngine.process("donor-report", context);

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = getPdfRendererBuilder();
                builder.withHtmlContent(htmlContent, "/");
                builder.toStream(os);
                builder.run();

                return os.toByteArray();
            }
        } catch (Exception e) {
            throw new PdfGenerationException("Ошибка при генерации PDF документа", e);
        }
    }

    private static PdfRendererBuilder getPdfRendererBuilder() {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();

        ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");
        if (fontResource.exists()) {
            builder.useFont(() -> {
                try {
                    return fontResource.getInputStream();
                } catch (Exception e) {
                    throw new PdfGenerationException("Не удалось прочитать файл шрифта", e);
                }
            }, "Roboto");
        }

        return builder;
    }
}
