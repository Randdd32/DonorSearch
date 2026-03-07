package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.service.hardware.VideoCardService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.VideoCardDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.VideoCardMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/video-cards")
public class VideoCardController extends AbstractReadController<VideoCardEntity, VideoCardDto, VideoCardService> {
    public VideoCardController(VideoCardService service, VideoCardMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<VideoCardDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> chipsetIds,
            @RequestParam(required = false) List<Long> memoryTypeIds,
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) Integer minTdp,
            @RequestParam(required = false) Integer maxTdp,
            @RequestParam(required = false) Integer slotWidth,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, manufacturerIds, chipsetIds, memoryTypeIds, minLength, maxLength, minTdp, maxTdp, slotWidth, pageable),
                toDtoMapper
        );
    }
}
