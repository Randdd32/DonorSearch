package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MonitorRepository extends HardwareRepository<MonitorEntity> {
    @Override
    @Query(value = "SELECT child.*, parent.* FROM component_monitor child " +
            "JOIN component parent ON child.id = parent.id " +
            "ORDER BY parent.search_name <-> :searchToken ASC LIMIT 1", nativeQuery = true)
    Optional<MonitorEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
