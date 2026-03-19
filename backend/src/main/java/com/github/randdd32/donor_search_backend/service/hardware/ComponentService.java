package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.ComponentRepository;
import com.github.randdd32.donor_search_backend.repository.hardware.ComponentScoreProjection;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ComponentService extends AbstractReadService<ComponentEntity, ComponentRepository> {
    public ComponentService(ComponentRepository repository) {
        super(repository, ComponentEntity.class);
    }

    @Transactional(readOnly = true)
    public Optional<ComponentScoreProjection> findBestMatchWithScore(String rawName, ComponentType type) {
        String cleanToken = QueryUtils.cleanSearchToken(rawName);
        if (cleanToken == null) {
            return Optional.empty();
        }
        return repository.findBestMatchWithScore(cleanToken, type.name());
    }
}
