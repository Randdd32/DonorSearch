package com.github.randdd32.donor_search_backend.repository;

import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRuleEntity, Long> {
    Optional<CompatibilityRuleEntity> findByRuleCodeIgnoreCase(String ruleCode);

    @Query("""
        SELECT r FROM CompatibilityRuleEntity r
        WHERE (
            :search IS NULL
            OR LOWER(r.ruleCode) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.expression) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.description) LIKE CONCAT('%', :search, '%')
          )
          AND (:isActive IS NULL OR r.isActive = :isActive)
          AND (CAST(:createdAfter AS timestamp) IS NULL OR r.createdAt >= :createdAfter)
          AND (CAST(:createdBefore AS timestamp) IS NULL OR r.createdAt <= :createdBefore)
          AND (CAST(:updatedAfter AS timestamp) IS NULL OR r.updatedAt >= :updatedAfter)
          AND (CAST(:updatedBefore AS timestamp) IS NULL OR r.updatedAt <= :updatedBefore)
    """)
    Page<CompatibilityRuleEntity> findByFilters(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            @Param("createdAfter") Instant createdAfter,
            @Param("createdBefore") Instant createdBefore,
            @Param("updatedAfter") Instant updatedAfter,
            @Param("updatedBefore") Instant updatedBefore,
            Pageable pageable
    );
}
