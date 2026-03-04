package com.github.randdd32.donor_search_backend.repository.dictionary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DictionaryRepository<T> extends JpaRepository<T, Long> {
    Page<T> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
