package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.repository.dictionary.DictionaryRepository;
import com.github.randdd32.donor_search_backend.service.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDictionaryService<T, R extends DictionaryRepository<T>> extends AbstractService<T, R> {
    @Transactional(readOnly = true)
    public Page<T> getAll(String search, Pageable pageable) {
        String cleanSearch = QueryUtils.cleanSearchToken(search);
        if (cleanSearch == null) {
            return getRepository().findAll(pageable);
        }
        return getRepository().findByNameContainingIgnoreCase(cleanSearch, pageable);
    }
}
