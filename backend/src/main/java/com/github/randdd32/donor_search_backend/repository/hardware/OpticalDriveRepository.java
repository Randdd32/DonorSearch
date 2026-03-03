package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OpticalDriveRepository extends JpaRepository<OpticalDriveEntity, Long> {
    @Query(value = "SELECT * FROM component_optical_drive " +
            "ORDER BY similarity(search_name, :searchToken) DESC LIMIT 1",
            nativeQuery = true)
    Optional<OpticalDriveEntity> findMostSimilar(@Param("searchToken") String searchToken);

    @Query("""
        SELECT od FROM OpticalDriveEntity od
        LEFT JOIN od.manufacturer m
        LEFT JOIN od.formFactor f
        LEFT JOIN od.storageInterface i
        WHERE (:search IS NULL OR LOWER(od.searchName) LIKE CONCAT('%', :search, '%'))
          AND (:manufacturerIds IS NULL OR m.id IN :manufacturerIds)
          AND (:formFactorIds IS NULL OR f.id IN :formFactorIds)
          AND (:interfaceIds IS NULL OR i.id IN :interfaceIds)
    """)
    Page<OpticalDriveEntity> findByFilters(
            @Param("search") String search,
            @Param("manufacturerIds") List<Long> manufacturerIds,
            @Param("formFactorIds") List<Long> formFactorIds,
            @Param("interfaceIds") List<Long> interfaceIds,
            Pageable pageable
    );
}
