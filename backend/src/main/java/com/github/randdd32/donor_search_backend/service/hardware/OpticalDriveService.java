package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.OpticalDriveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpticalDriveService {
    private final OpticalDriveRepository repository;

    @Transactional(readOnly = true)
    public Page<OpticalDriveEntity> getAll(String search, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("id"));
        if (search == null || search.isBlank()) {
            return repository.findAll(pageRequest);
        }
        return repository.findBySearchName(search, pageRequest);
    }

    @Transactional(readOnly = true)
    public OpticalDriveEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(OpticalDriveEntity.class, id));
    }

    @Transactional(readOnly = true)
    public OpticalDriveEntity findBestMatch(String rawNameFromInventory) {
        String cleanToken = rawNameFromInventory.toLowerCase().trim();
        return repository.findMostSimilar(cleanToken)
                .orElseThrow(() -> new IllegalArgumentException("No suitable optical drive found for: " + rawNameFromInventory));
    }
}
