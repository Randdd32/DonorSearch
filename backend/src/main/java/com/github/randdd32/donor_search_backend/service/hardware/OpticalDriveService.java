package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.OpticalDriveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpticalDriveService {
    private final OpticalDriveRepository repository;

    @Transactional(readOnly = true)
    public Page<OpticalDriveEntity> getAll(
            String search,
            List<Long> manufacturerIds,
            List<Long> formFactorIds,
            List<Long> interfaceIds,
            List<String> sort,
            int page,
            int size) {
        Sort appliedSort = QueryUtils.createSort(sort, Sort.by(Sort.Direction.ASC, "id"));
        Pageable pageRequest = PageRequest.of(page, size, appliedSort);

        return repository.findByFilters(
                QueryUtils.cleanSearchToken(search),
                QueryUtils.nullIfEmpty(manufacturerIds),
                QueryUtils.nullIfEmpty(formFactorIds),
                QueryUtils.nullIfEmpty(interfaceIds),
                pageRequest
        );
    }

    @Transactional(readOnly = true)
    public OpticalDriveEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(OpticalDriveEntity.class, id));
    }

    @Transactional(readOnly = true)
    public OpticalDriveEntity findBestMatch(String rawNameFromInventory) {
        String cleanToken = QueryUtils.cleanSearchToken(rawNameFromInventory);
        if (cleanToken == null) {
            throw new IllegalArgumentException("Search token cannot be null or empty");
        }
        return repository.findMostSimilar(cleanToken)
                .orElseThrow(() -> new IllegalArgumentException("No suitable optical drive found for: " + rawNameFromInventory));
    }
}
