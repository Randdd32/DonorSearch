package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OpticalDriveRepository extends HardwareRepository<OpticalDriveEntity> {
    @Override
    @Query(value = "SELECT * FROM component_optical_drive " +
            "ORDER BY similarity(search_name, :searchToken) DESC LIMIT 1",
            nativeQuery = true)
    Optional<OpticalDriveEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
