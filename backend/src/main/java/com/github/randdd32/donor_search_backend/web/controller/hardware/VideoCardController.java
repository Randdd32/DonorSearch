package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.log.NoLogging;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.service.hardware.VideoCardService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.filter.VideoCardFilter;
import com.github.randdd32.donor_search_backend.web.dto.hardware.VideoCardDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.VideoCardMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/video-cards")
public class VideoCardController extends AbstractReadController<VideoCardEntity, VideoCardDto, VideoCardService> {
    public VideoCardController(VideoCardService service, VideoCardMapper mapper) {
        super(service, mapper::toDto);
    }

    @NoLogging
    @GetMapping
    public PageDto<VideoCardDto> getAll(
            @ModelAttribute VideoCardFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(service.getAll(filter, pageable), toDtoMapper);
    }
}
