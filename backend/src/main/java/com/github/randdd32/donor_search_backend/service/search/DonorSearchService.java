package com.github.randdd32.donor_search_backend.service.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.randdd32.donor_search_backend.core.error.HardRejectException;
import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.service.compatibility.CompatibilityEngineService;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.PcBuildContext;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.dto.search.CompatibleComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorWarningDto;
import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonorSearchService {
    private final InfraDeviceService infraDeviceService;
    private final IntegrationMappingService mappingService;
    private final CompatibilityEngineService compatibilityEngine;
    private final Cache<@NonNull  String, List<DonorResultDto>> searchCache;

    public String runSearch(Long targetDeviceId, Long targetAdapterId) {
        ExternalDeviceDto targetDevice = infraDeviceService.getDeviceDetails(targetDeviceId);
        ExternalComponentDto targetComponent = targetDevice.components().stream()
                .filter(c -> c.adapterId().equals(targetAdapterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target adapter not found in device"));

        ExternalComponentCategory targetCompCategory = targetComponent.category();
        if (targetCompCategory == ExternalComponentCategory.UNKNOWN) {
            throw new IllegalArgumentException("Target component category is UNKNOWN. Cannot proceed");
        }

        ComponentType targetType = ComponentType.valueOf(targetCompCategory.name());

        PcBuildContext targetContext = new PcBuildContext();
        for (ExternalComponentDto comp : targetDevice.components()) {
            if (comp.adapterId().equals(targetAdapterId))
                continue;

            ComponentEntity internalEntity = resolveAndMapComponent(comp.externalName(),
                    ComponentType.valueOf(comp.category().name()), null);
            if (internalEntity != null) {
                targetContext.putComponent(internalEntity);
            }
        }

        List<ExternalDeviceDto> candidateDevices = infraDeviceService.getPotentialDonors(targetDeviceId, targetCompCategory);
        List<DonorResultDto> finalResults = new ArrayList<>();

        for (ExternalDeviceDto device : candidateDevices) {
            List<CompatibleComponentDto> validComponentsInDevice = new ArrayList<>();

            for (ExternalComponentDto rawDonorComp : device.components()) {
                if (rawDonorComp.category() != targetCompCategory)
                    continue;

                List<DonorWarningDto> compWarnings = new ArrayList<>();
                int compPenalty = 0;

                ComponentEntity internalDonor = resolveAndMapComponent(rawDonorComp.externalName(), targetType, compWarnings);

                if (internalDonor == null) {
                    compPenalty += WarningSeverity.HIGH.getWeight();
                    compWarnings.add(new DonorWarningDto("Характеристики детали-донора отсутствуют в БД. Проверка совместимости невозможна.",
                            WarningSeverity.HIGH));
                } else {
                    PcBuildContext virtualBuild = targetContext.copy();
                    virtualBuild.putComponent(internalDonor);

                    try {
                        List<DonorWarningDto> ruleWarnings = compatibilityEngine.evaluateCompatibility(virtualBuild, targetType);
                        compWarnings.addAll(ruleWarnings);
                        for (DonorWarningDto w : ruleWarnings) {
                            compPenalty += w.severity().getWeight();
                        }
                    } catch (HardRejectException e) {
                        continue;
                    }
                }

                validComponentsInDevice.add(new CompatibleComponentDto(
                        rawDonorComp, internalDonor, compPenalty, compWarnings
                ));
            }

            if (!validComponentsInDevice.isEmpty()) {
                int devicePenalty = device.lifeCycleState().getPenaltyWeight();
                int minCompPenalty = validComponentsInDevice.stream().mapToInt(CompatibleComponentDto::componentPenalty).min().orElse(0);

                finalResults.add(new DonorResultDto(
                        device, devicePenalty, devicePenalty + minCompPenalty, validComponentsInDevice
                ));
            }
        }

        String searchSessionId = UUID.randomUUID().toString();
        searchCache.put(searchSessionId, finalResults);
        return searchSessionId;
    }

    public PageDto<DonorResultDto> getResults(
            String sessionId, String search, List<ExternalDeviceState> states,
            List<Long> departmentIds, List<Long> buildingIds, List<Long> floorIds, List<Long> roomIds,
            List<Long> deviceManufacturerIds, List<Long> typeIds, List<Long> modelIds,
            Instant dateReceivedFrom, Instant dateReceivedTo, Boolean isWorking,
            List<Long> componentManufacturerIds, Integer maxTotalPenalty, Pageable pageable) {

        List<DonorResultDto> cachedResults = searchCache.getIfPresent(sessionId);
        if (cachedResults == null) {
            throw new NotFoundException(String.format("Сессия поиска [%s] истекла или не найдена. Запустите поиск заново.", sessionId));
        }

        List<DonorResultDto> filtered = cachedResults.stream()
                .filter(d -> matchesFilters(d, search, states, departmentIds, buildingIds, floorIds, roomIds,
                        deviceManufacturerIds, typeIds, modelIds, dateReceivedFrom, dateReceivedTo,
                        isWorking, componentManufacturerIds, maxTotalPenalty))
                .toList();

        Comparator<DonorResultDto> comparator = Comparator.comparingInt(DonorResultDto::totalPenalty);
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            comparator = switch (order.getProperty()) {
                case "id" -> Comparator.comparingLong(d -> d.donorDevice().externalId());
                case "name" -> Comparator.comparing(d -> d.donorDevice().name());
                case "inventoryNumber" -> Comparator.comparing(d -> d.donorDevice().inventoryNumber(), Comparator.nullsLast(String::compareTo));
                case "dateReceived" -> Comparator.comparing(d -> d.donorDevice().dateReceived(), Comparator.nullsLast(java.time.Instant::compareTo));
                default -> Comparator.comparingInt(DonorResultDto::totalPenalty);
            };
            if (order.getDirection() == Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }
        }

        List<DonorResultDto> sorted = filtered.stream().sorted(comparator).toList();

        int totalCount = sorted.size();
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int fromIndex = Math.min(page * size, totalCount);
        int toIndex = Math.min(fromIndex + size, totalCount);

        return PageDtoMapper.toDto(sorted.subList(fromIndex, toIndex), totalCount, page, size);
    }

    private boolean matchesFilters(
            DonorResultDto dto, String search, List<ExternalDeviceState> states,
            List<Long> departmentIds, List<Long> buildingIds, List<Long> floorIds, List<Long> roomIds,
            List<Long> deviceManufacturerIds, List<Long> typeIds, List<Long> modelIds,
            Instant dateReceivedFrom, Instant dateReceivedTo, Boolean isWorking,
            List<Long> componentManufacturerIds, Integer maxTotalPenalty) {
        ExternalDeviceDto dev = dto.donorDevice();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase().trim();
            boolean matchName = dev.name() != null && dev.name().toLowerCase().contains(q);
            boolean matchInv = dev.inventoryNumber() != null && dev.inventoryNumber().toLowerCase().contains(q);
            boolean matchSn = dev.serialNumber() != null && dev.serialNumber().toLowerCase().contains(q);
            if (!matchName && !matchInv && !matchSn) return false;
        }

        if (!CollectionUtils.isEmpty(states) && !states.contains(dev.lifeCycleState())) return false;
        if (!CollectionUtils.isEmpty(departmentIds) && !departmentIds.contains(dev.departmentId())) return false;
        if (!CollectionUtils.isEmpty(deviceManufacturerIds) && !deviceManufacturerIds.contains(dev.manufacturerId())) return false;
        if (!CollectionUtils.isEmpty(typeIds) && !typeIds.contains(dev.typeId())) return false;
        if (!CollectionUtils.isEmpty(modelIds) && !modelIds.contains(dev.modelId())) return false;
        if (!CollectionUtils.isEmpty(buildingIds) && !buildingIds.contains(dev.buildingId())) return false;
        if (!CollectionUtils.isEmpty(floorIds) && !floorIds.contains(dev.floorId())) return false;
        if (!CollectionUtils.isEmpty(roomIds) && !roomIds.contains(dev.roomId())) return false;

        if (isWorking != null && !isWorking.equals(dev.isWorking())) return false;
        if (dateReceivedFrom != null && dev.dateReceived() != null && dev.dateReceived().isBefore(dateReceivedFrom)) return false;
        if (dateReceivedTo != null && dev.dateReceived() != null && dev.dateReceived().isAfter(dateReceivedTo)) return false;
        if (maxTotalPenalty != null && dto.totalPenalty() > maxTotalPenalty) return false;

        if (!org.springframework.util.CollectionUtils.isEmpty(componentManufacturerIds)) {
            return dto.compatibleComponents().stream()
                    .anyMatch(c -> c.externalInfo().manufacturerId() != null &&
                            componentManufacturerIds.contains(c.externalInfo().manufacturerId()));
        }

        return true;
    }

    private ComponentEntity resolveAndMapComponent(String externalName, ComponentType type, List<DonorWarningDto> warningsListToAppend) {
        IntegrationMappingEntity mapping = mappingService.resolveAndSaveMapping(externalName, type);
        if (mapping == null) return null;

        if (warningsListToAppend != null) {
            if (mapping.getConfidence() == MappingConfidence.NEEDS_REVIEW) {
                warningsListToAppend.add(new DonorWarningDto("Сопоставление детали не подтверждено администратором.", WarningSeverity.LOW));
            }
            if (mapping.getConfidence() == MappingConfidence.BAD_MATCH) {
                warningsListToAppend.add(new DonorWarningDto("Деталь распознана с низким процентом уверенности. Возможна ошибка!", WarningSeverity.HIGH));
            }
        }
        return mapping.getInternalComponent();
    }
}
