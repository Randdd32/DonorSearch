package com.github.randdd32.donor_search_backend.service.log;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.web.dto.log.LogFileDto;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LogService {
    private static final Path LOG_DIR = Paths.get("logs").toAbsolutePath().normalize();
    private static final String ACTIVE_LOG_NAME = "application.log";

    public List<LogFileDto> getAvailableLogFiles() throws IOException {
        if (!Files.exists(LOG_DIR)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(LOG_DIR)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("application"))
                    .sorted(Comparator.comparing(this::getLastModifiedSafe).reversed())
                    .map(path -> {
                        try {
                            return new LogFileDto(
                                    path.getFileName().toString(),
                                    Files.size(path),
                                    Files.getLastModifiedTime(path).toInstant()
                            );
                        } catch (IOException e) {
                            return new LogFileDto(path.getFileName().toString(), 0, Instant.EPOCH);
                        }
                    })
                    .toList();
        }
    }

    public List<String> getTail(String filename, int linesCount) throws IOException {
        if (linesCount < 1 || linesCount > 5000) {
            throw new IllegalArgumentException("Lines count must be between 1 and 5000");
        }

        Path filePath = resolveAndValidatePath(filename);

        List<String> tail = new ArrayList<>(linesCount);
        try (ReversedLinesFileReader reader = ReversedLinesFileReader.builder()
                .setPath(filePath)
                .setCharset(StandardCharsets.UTF_8)
                .get()) {
            String line;
            while ((line = reader.readLine()) != null && tail.size() < linesCount) {
                tail.add(line);
            }
        }
        Collections.reverse(tail);
        return tail;
    }

    public Path getLogFilePath(String filename) {
        return resolveAndValidatePath(filename);
    }

    private Path resolveAndValidatePath(String filename) {
        String safeFilename = (filename == null || filename.isBlank()) ? ACTIVE_LOG_NAME : filename;
        Path resolvedPath = LOG_DIR.resolve(safeFilename).normalize().toAbsolutePath();

        if (!resolvedPath.startsWith(LOG_DIR)) {
            throw new SecurityException("Access denied: attempt to bypass log directory boundaries");
        }
        if (!Files.exists(resolvedPath)) {
            throw new NotFoundException("Log file not found: " + safeFilename);
        }
        return resolvedPath;
    }

    private FileTime getLastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            return FileTime.fromMillis(0);
        }
    }
}
