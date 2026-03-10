package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CaseFanRepository extends HardwareRepository<CaseFanEntity> {
    @Override
    @Query(value = "SELECT child.*, parent.* FROM component_case_fan child " +
            "JOIN component parent ON child.id = parent.id " +
            "ORDER BY parent.search_name <-> :searchToken ASC LIMIT 1",
            nativeQuery = true)
    Optional<CaseFanEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
