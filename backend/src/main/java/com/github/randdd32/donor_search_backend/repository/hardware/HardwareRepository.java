package com.github.randdd32.donor_search_backend.repository.hardware;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface HardwareRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {}
