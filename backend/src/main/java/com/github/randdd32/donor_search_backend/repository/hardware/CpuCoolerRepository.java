package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CpuCoolerRepository extends HardwareRepository<CpuCoolerEntity> {
    @Override
    @Query(value = "SELECT child.*, parent.* FROM component_cpu_cooler child " +
            "JOIN component parent ON child.id = parent.id " +
            "ORDER BY parent.search_name <-> :searchToken ASC LIMIT 1", nativeQuery = true)
    Optional<CpuCoolerEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
