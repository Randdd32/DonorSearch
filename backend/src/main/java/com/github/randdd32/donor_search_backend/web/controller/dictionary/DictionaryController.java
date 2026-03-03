package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.dictionary.ManufacturerService;
import com.github.randdd32.donor_search_backend.service.dictionary.OpticalDriveFormFactorService;
import com.github.randdd32.donor_search_backend.service.dictionary.StorageInterfaceService;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.NamedDictionaryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + "/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final ManufacturerService manufacturerService;
    private final StorageInterfaceService storageInterfaceService;
    private final OpticalDriveFormFactorService odFormFactorService;

    private final DictionaryMapper mapper;

    @GetMapping("/manufacturers")
    public PageDto<NamedDictionaryDto> getManufacturers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return PageDtoMapper.toDto(manufacturerService.getAll(search, page, size), mapper::toDto);
    }

    @GetMapping("/storage-interfaces")
    public PageDto<NamedDictionaryDto> getStorageInterfaces(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return PageDtoMapper.toDto(storageInterfaceService.getAll(search, page, size), mapper::toDto);
    }

    @GetMapping("/optical-drive-form-factors")
    public PageDto<NamedDictionaryDto> getOpticalDriveFormFactors(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return PageDtoMapper.toDto(odFormFactorService.getAll(search, page, size), mapper::toDto);
    }
}
