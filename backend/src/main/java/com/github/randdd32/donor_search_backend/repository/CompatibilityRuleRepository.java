package com.github.randdd32.donor_search_backend.repository;

import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRuleEntity, Long> {
    Optional<CompatibilityRuleEntity> findByRuleCodeIgnoreCase(String ruleCode);

    @Query("SELECT r FROM CompatibilityRuleEntity r WHERE " +
            "LOWER(r.ruleCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.expression) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CompatibilityRuleEntity> findBySearchToken(@Param("search") String search, Pageable pageable);
}
