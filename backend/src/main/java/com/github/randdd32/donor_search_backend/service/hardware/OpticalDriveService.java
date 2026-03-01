package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.OpticalDriveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpticalDriveService {
    private final OpticalDriveRepository repository;

    @Transactional(readOnly = true)
    public OpticalDriveEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(OpticalDriveEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<OpticalDriveEntity> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public OpticalDriveEntity findBestMatch(String rawNameFromInventory) {
        String cleanToken = rawNameFromInventory.toLowerCase().trim();
        return repository.findMostSimilar(cleanToken)
                .orElseThrow(() -> new IllegalArgumentException("Не найдено подходящих приводов для: " + rawNameFromInventory));
    }
}
