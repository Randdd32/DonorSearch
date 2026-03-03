package com.github.randdd32.donor_search_backend.repository.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorageInterfaceRepository extends JpaRepository<StorageInterfaceEntity, Long> {
    @Query("SELECT i FROM StorageInterfaceEntity i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<StorageInterfaceEntity> findByName(@Param("search") String search, Pageable pageable);
}
