package com.github.randdd32.donor_search_backend.repository;

import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntegrationMappingRepository extends JpaRepository<IntegrationMappingEntity, Long>,
        JpaSpecificationExecutor<IntegrationMappingEntity> {
    Optional<IntegrationMappingEntity> findByExternalNameIgnoreCase(String externalName);

    /**
     * Возвращает Object[], где [0] = externalName, [1] = internalComponent.id.
     */
    @Query("SELECT m.externalName, m.internalComponent.id FROM IntegrationMappingEntity m WHERE LOWER(m.externalName) IN :names")
    List<Object[]> findMappedIdsByNames(@Param("names") List<String> lowerNames);
}
