package com.github.randdd32.donor_search_backend.web.controller.log;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.log.NoLogging;
import com.github.randdd32.donor_search_backend.service.log.LogService;
import com.github.randdd32.donor_search_backend.web.dto.log.LogFileDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@NoLogging
@RestController
@RequestMapping(Constants.API_URL + "/logs")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
@RequiredArgsConstructor
@Validated
public class LogController {
    private final LogService logService;

    @GetMapping("/files")
    public List<LogFileDto> getAvailableFiles() throws IOException {
        return logService.getAvailableLogFiles();
    }

    @GetMapping("/tail")
    public Map<String, List<String>> getLogTail(
            @RequestParam(required = false) String filename,
            @RequestParam(defaultValue = "1000") @Min(1) @Max(5000) int lines) throws IOException {

        return Map.of("lines", logService.getTail(filename, lines));
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadLog(@RequestParam(required = false) String filename) throws IOException {
        Path path = logService.getLogFilePath(filename);

        Resource resource = new FileSystemResource(path);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(Files.size(path))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
