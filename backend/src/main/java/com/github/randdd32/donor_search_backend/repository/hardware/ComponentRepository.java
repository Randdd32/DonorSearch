package com.github.randdd32.donor_search_backend.repository.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends HardwareRepository<ComponentEntity> {
    @Override
    @Query(value = "SELECT * FROM component ORDER BY search_name <-> :searchToken ASC LIMIT 1",
            nativeQuery = true)
    Optional<ComponentEntity> findMostSimilar(@Param("searchToken") String searchToken);
}
