package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemoryRepository extends HardwareRepository<MemoryEntity> {
    @Override
    @Query(value = "SELECT child.*, parent.* FROM component_memory child " +
            "JOIN component parent ON child.id = parent.id " +
            "ORDER BY parent.search_name <-> :searchToken ASC LIMIT 1",
            nativeQuery = true)
    Optional<MemoryEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
