package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.VideoCardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.VideoCardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.VideoCardFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoCardService extends AbstractReadService<VideoCardEntity, VideoCardRepository> {
    public VideoCardService(VideoCardRepository repository) {
        super(repository, VideoCardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<VideoCardEntity> getAll(VideoCardFilter filter, Pageable pageable) {
        Specification<VideoCardEntity> spec = VideoCardSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
