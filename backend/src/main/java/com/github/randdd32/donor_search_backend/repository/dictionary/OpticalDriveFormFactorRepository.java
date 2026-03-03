package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpticalDriveFormFactorRepository extends JpaRepository<OpticalDriveFormFactorEntity, Long> {
    Page<OpticalDriveFormFactorEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
