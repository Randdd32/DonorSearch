package com.github.randdd32.donor_search_backend.repository;

import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IntegrationMappingRepository extends JpaRepository<IntegrationMappingEntity, Long>,
        JpaSpecificationExecutor<IntegrationMappingEntity> {
    Optional<IntegrationMappingEntity> findByExternalNameIgnoreCase(String externalName);
}
