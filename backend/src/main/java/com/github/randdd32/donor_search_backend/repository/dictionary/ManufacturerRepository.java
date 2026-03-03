package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Long> {
    Page<ManufacturerEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
