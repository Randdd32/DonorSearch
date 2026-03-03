package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Long> {
    @Query("SELECT m FROM ManufacturerEntity m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ManufacturerEntity> findByName(@Param("search") String search, Pageable pageable);
}
