package com.github.randdd32.donor_search_backend.web.controller.hardware;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.service.hardware.ExpansionCardService;
import com.github.randdd32.donor_search_backend.web.controller.AbstractReadController;
import com.github.randdd32.donor_search_backend.web.dto.hardware.ExpansionCardDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.ExpansionCardMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.COMPONENTS_URL + "/expansion-cards")
public class ExpansionCardController extends AbstractReadController<ExpansionCardEntity, ExpansionCardDto, ExpansionCardService> {
    public ExpansionCardController(ExpansionCardService service, ExpansionCardMapper mapper) {
        super(service, mapper::toDto);
    }

    @GetMapping
    public PageDto<ExpansionCardDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ExpansionCardType cardType,
            @RequestParam(required = false) List<Long> manufacturerIds,
            @RequestParam(required = false) List<Long> interfaceIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) List<Long> audioChipsetIds,
            @RequestParam(required = false) List<Long> protocolIds,
            @RequestParam(required = false) Double minChannels,
            @RequestParam(required = false) Double maxChannels,
            @RequestParam(required = false) Integer minDigitalAudioBit,
            @RequestParam(required = false) Integer maxDigitalAudioBit,
            @RequestParam(required = false) Double minSampleRateKhz,
            @RequestParam(required = false) Double maxSampleRateKhz,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable) {

        return PageDtoMapper.toDto(
                service.getAll(search, cardType, manufacturerIds, interfaceIds, colorIds, audioChipsetIds, protocolIds,
                        minChannels, maxChannels, minDigitalAudioBit, maxDigitalAudioBit,
                        minSampleRateKhz, maxSampleRateKhz, pageable),
                toDtoMapper
        );
    }
}
