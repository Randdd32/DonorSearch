package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OpticalDriveRepository extends JpaRepository<OpticalDriveEntity, Long> {
    @Query(value = "SELECT * FROM component_optical_drive " +
            "ORDER BY similarity(search_name, :searchToken) DESC LIMIT 1",
            nativeQuery = true)
    Optional<OpticalDriveEntity> findMostSimilar(@Param("searchToken") String searchToken);

    @Query("SELECT od FROM OpticalDriveEntity od WHERE " +
            "LOWER(od.searchName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<OpticalDriveEntity> findBySearchName(@Param("search") String search, Pageable pageable);
}
