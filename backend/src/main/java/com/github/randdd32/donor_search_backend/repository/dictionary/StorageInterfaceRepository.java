package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageInterfaceRepository extends JpaRepository<StorageInterfaceEntity, Long> {
    Page<StorageInterfaceEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
