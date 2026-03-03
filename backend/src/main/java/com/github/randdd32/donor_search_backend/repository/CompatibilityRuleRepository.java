package com.github.randdd32.donor_search_backend.repository;

import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRuleEntity, Long>,
        JpaSpecificationExecutor<CompatibilityRuleEntity> {
    Optional<CompatibilityRuleEntity> findByRuleCodeIgnoreCase(String ruleCode);
}
