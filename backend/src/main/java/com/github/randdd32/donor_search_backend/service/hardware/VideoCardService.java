package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.VideoCardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.VideoCardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoCardService extends AbstractReadService<VideoCardEntity, VideoCardRepository> {
    public VideoCardService(VideoCardRepository repository) {
        super(repository, VideoCardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<VideoCardEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> chipsetIds, List<Long> memoryTypeIds,
            Integer minLength, Integer maxLength,
            Integer minTdp, Integer maxTdp,
            Integer slotWidth, Pageable pageable) {

        Specification<VideoCardEntity> spec = VideoCardSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, chipsetIds, memoryTypeIds,
                minLength, maxLength, minTdp, maxTdp, slotWidth
        );
        return repository.findAll(spec, pageable);
    }
}
