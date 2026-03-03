package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.StorageInterfaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorageInterfaceService {
    private final StorageInterfaceRepository repository;

    @Transactional(readOnly = true)
    public Page<StorageInterfaceEntity> getAll(String search, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("name"));
        String cleanSearch = QueryUtils.cleanSearchToken(search);
        if (cleanSearch == null) {
            return repository.findAll(pageRequest);
        }
        return repository.findByName(cleanSearch, pageRequest);
    }

    @Transactional(readOnly = true)
    public StorageInterfaceEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(StorageInterfaceEntity.class, id));
    }
}
