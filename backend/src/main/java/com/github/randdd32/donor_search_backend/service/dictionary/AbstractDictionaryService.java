package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.repository.dictionary.DictionaryRepository;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public abstract class AbstractDictionaryService<T, R extends DictionaryRepository<T>> extends AbstractReadService<T, R> {
    protected AbstractDictionaryService(R repository, Class<T> entityClass) {
        super(repository, entityClass);
    }

    @Transactional(readOnly = true)
    public Page<T> getAll(String search, Pageable pageable) {
        String cleanSearch = QueryUtils.cleanSearchToken(search);
        if (cleanSearch == null) {
            return repository.findAll(pageable);
        }
        return repository.findByNameContainingIgnoreCase(cleanSearch, pageable);
    }

    @Transactional(readOnly = true)
    public List<T> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return repository.findAllById(ids);
    }
}
