package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpticalDriveFormFactorRepository extends JpaRepository<OpticalDriveFormFactorEntity, Long> {
    @Query("SELECT ff FROM OpticalDriveFormFactorEntity ff WHERE LOWER(ff.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<OpticalDriveFormFactorEntity> findByName(@Param("search") String search, Pageable pageable);
}
