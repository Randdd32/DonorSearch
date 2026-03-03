package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManufacturerService {
    private final ManufacturerRepository repository;

    @Transactional(readOnly = true)
    public Page<ManufacturerEntity> getAll(String search, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("name"));
        String cleanSearch = QueryUtils.cleanSearchToken(search);
        if (cleanSearch == null) {
            return repository.findAll(pageRequest);
        }
        return repository.findByNameContainingIgnoreCase(cleanSearch, pageRequest);
    }

    @Transactional(readOnly = true)
    public ManufacturerEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ManufacturerEntity.class, id));
    }
}
