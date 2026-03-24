package com.github.randdd32.donor_search_backend.service.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.randdd32.donor_search_backend.core.error.HardRejectException;
import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.service.compatibility.CompatibilityEngineService;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.service.compatibility.context.PcBuildContext;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.dto.search.CompatibleComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorWarningDto;
import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;
import com.github.randdd32.donor_search_backend.web.mapper.hardware.ComponentDtoMapperFacade;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonorSearchService {
    private final InfraDeviceService infraDeviceService;
    private final IntegrationMappingService mappingService;
    private final CompatibilityEngineService compatibilityEngine;
    private final ComponentDtoMapperFacade componentDtoMapper;
    private final Cache<@NonNull  String, List<DonorResultDto>> searchCache;

    @Transactional(readOnly = true)
    public String runSearch(Long targetDeviceId, Long targetAdapterId) {
        ExternalDeviceDto targetDevice = infraDeviceService.getDeviceDetails(targetDeviceId);
        ExternalComponentDto targetComponent = targetDevice.components().stream()
                .filter(c -> c.adapterId().equals(targetAdapterId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target adapter not found in device"));

        if (targetComponent.category() == ExternalComponentCategory.UNKNOWN) {
            throw new IllegalArgumentException("Target component category is UNKNOWN. Cannot proceed");
        }
        ComponentType targetType = ComponentType.valueOf(targetComponent.category().name());

        PcBuildContext targetContext = new PcBuildContext();
        for (ExternalComponentDto comp : targetDevice.components()) {
            if (comp.adapterId().equals(targetAdapterId)) {
                continue;
            }
            ComponentEntity internalEntity = resolveTargetComponent(comp.externalName(), ComponentType.valueOf(comp.category().name()));
            if (internalEntity != null) {
                targetContext.putComponent(internalEntity);
            }
        }

        List<ExternalDeviceDto> candidateDevices = infraDeviceService.getPotentialDonors(targetDeviceId, targetComponent.category());

        Set<String> uniqueDonorExternalNames = candidateDevices.stream()
                .flatMap(d -> d.components().stream())
                .filter(c -> c.category() == targetComponent.category())
                .map(ExternalComponentDto::externalName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toSet());

        Map<String, IntegrationMappingEntity> bulkMappings = mappingService.resolveAndSaveBatch(uniqueDonorExternalNames, targetType);

        List<DonorResultDto> finalResults = candidateDevices.stream()
                .map(device -> evaluateDevice(device, targetType, targetContext, bulkMappings))
                .filter(Objects::nonNull)
                .toList();

        String sessionId = UUID.randomUUID().toString();
        searchCache.put(sessionId, finalResults);
        return sessionId;
    }

    public PageDto<DonorResultDto> getResults(
            String sessionId, String search, List<Long> stateIds,
            List<Long> departmentIds, List<Long> buildingIds, List<Long> floorIds, List<Long> roomIds,
            List<Long> deviceManufacturerIds, List<Long> typeIds, List<Long> modelIds,
            Instant dateReceivedFrom, Instant dateReceivedTo, Boolean isWorking,
            List<Long> componentManufacturerIds, Integer maxTotalPenalty, Pageable pageable) {
        List<DonorResultDto> cachedResults = searchCache.getIfPresent(sessionId);
        if (cachedResults == null) {
            throw new NotFoundException(String.format("Сессия поиска [%s] истекла или не найдена. Запустите поиск заново.", sessionId));
        }

        List<DonorResultDto> filtered = cachedResults.stream()
                .filter(d -> matchesFilters(d, search, stateIds, departmentIds, buildingIds, floorIds, roomIds,
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
            DonorResultDto dto, String search, List<Long> stateIds,
            List<Long> departmentIds, List<Long> buildingIds, List<Long> floorIds, List<Long> roomIds,
            List<Long> deviceManufacturerIds, List<Long> typeIds, List<Long> modelIds,
            Instant dateReceivedFrom, Instant dateReceivedTo, Boolean isWorking,
            List<Long> componentManufacturerIds, Integer maxTotalPenalty) {
        ExternalDeviceDto dev = dto.donorDevice();

        String q = QueryUtils.cleanSearchToken(search);
        if (q != null) {
            boolean matchName = dev.name() != null && dev.name().toLowerCase().contains(q);
            boolean matchInv = dev.inventoryNumber() != null && dev.inventoryNumber().toLowerCase().contains(q);
            boolean matchSn = dev.serialNumber() != null && dev.serialNumber().toLowerCase().contains(q);
            if (!matchName && !matchInv && !matchSn) return false;
        }

        if (!isMatch(stateIds, dev.stateId())) return false;
        if (!isMatch(departmentIds, dev.departmentId())) return false;
        if (!isMatch(deviceManufacturerIds, dev.manufacturerId())) return false;
        if (!isMatch(typeIds, dev.typeId())) return false;
        if (!isMatch(modelIds, dev.modelId())) return false;
        if (!isMatch(buildingIds, dev.buildingId())) return false;
        if (!isMatch(floorIds, dev.floorId())) return false;
        if (!isMatch(roomIds, dev.roomId())) return false;

        if (isWorking != null && !isWorking.equals(dev.isWorking())) return false;
        if (dateReceivedFrom != null && dev.dateReceived() != null && dev.dateReceived().isBefore(dateReceivedFrom)) return false;
        if (dateReceivedTo != null && dev.dateReceived() != null && dev.dateReceived().isAfter(dateReceivedTo)) return false;
        if (maxTotalPenalty != null && dto.totalPenalty() > maxTotalPenalty) return false;

        if (!CollectionUtils.isEmpty(componentManufacturerIds)) {
            return dto.compatibleComponents().stream()
                    .anyMatch(c -> c.externalInfo().manufacturerId() != null &&
                            componentManufacturerIds.contains(c.externalInfo().manufacturerId()));
        }

        return true;
    }

    private <T> boolean isMatch(Collection<T> filterCollection, T value) {
        return CollectionUtils.isEmpty(filterCollection) || filterCollection.contains(value);
    }

    private ComponentEntity resolveTargetComponent(String extName, ComponentType expectedType) {
        IntegrationMappingEntity mapping = mappingService.resolveAndSaveMapping(extName, expectedType);
        return mapping != null ? mapping.getInternalComponent() : null;
    }

    private DonorResultDto evaluateDevice(ExternalDeviceDto device, ComponentType targetType,
            PcBuildContext targetContext, Map<String, IntegrationMappingEntity> bulkMappings) {
        List<CompatibleComponentDto> validComponents = new ArrayList<>();

        for (ExternalComponentDto rawDonorComp : device.components()) {
            if (!rawDonorComp.category().name().equals(targetType.name())
                || rawDonorComp.externalName() == null
                || rawDonorComp.externalName().isBlank()) {
                continue;
            }

            List<DonorWarningDto> compWarnings = new ArrayList<>();
            int compPenalty = 0;
            ComponentEntity internalDonor = null;

            IntegrationMappingEntity mapping = bulkMappings.get(rawDonorComp.externalName().toLowerCase().trim());

            if (mapping == null) {
                compPenalty += WarningSeverity.CRITICAL.getWeight();
                compWarnings.add(new DonorWarningDto(
                        "Характеристики детали-донора отсутствуют в БД. Автоматическая проверка совместимости невозможна! Требуется ручная проверка.",
                        WarningSeverity.CRITICAL
                ));
            } else {
                internalDonor = mapping.getInternalComponent();
                MappingConfidence confidence = mapping.getConfidence();

                if (confidence == MappingConfidence.AUTO) {
                    compPenalty += WarningSeverity.LOW.getWeight();
                    compWarnings.add(new DonorWarningDto("Деталь сопоставлена автоматически с высоким процентом уверенности. " +
                            "Тем не менее рекомендуется проверка администратора.",
                            WarningSeverity.LOW));
                } else if (confidence == MappingConfidence.NEEDS_REVIEW) {
                    compPenalty += WarningSeverity.MEDIUM.getWeight();
                    compWarnings.add(new DonorWarningDto("Сопоставление детали не подтверждено администратором.",
                            WarningSeverity.MEDIUM));
                } else if (confidence == MappingConfidence.BAD_MATCH) {
                    compPenalty += WarningSeverity.HIGH.getWeight();
                    compWarnings.add(new DonorWarningDto("Деталь распознана с низким процентом уверенности. Возможна ошибка!",
                            WarningSeverity.HIGH));
                }

                PcBuildContext virtualBuild = targetContext.copy();
                virtualBuild.putComponent(internalDonor);

                try {
                    List<DonorWarningDto> ruleWarnings = compatibilityEngine.evaluateCompatibility(virtualBuild, targetType);
                    compWarnings.addAll(ruleWarnings);
                    compPenalty += ruleWarnings.stream().mapToInt(w -> w.severity().getWeight()).sum();
                } catch (HardRejectException e) {
                    continue;
                }
            }

            Object internalDonorDto = componentDtoMapper.toDto(internalDonor);
            validComponents.add(new CompatibleComponentDto(rawDonorComp, internalDonorDto, compPenalty, compWarnings));
        }

        if (validComponents.isEmpty()) {
            return null;
        }

        int devicePenalty = device.lifeCycleState().getPenaltyWeight();
        int minCompPenalty = validComponents.stream().mapToInt(CompatibleComponentDto::componentPenalty).min().orElse(0);

        return new DonorResultDto(device, devicePenalty, devicePenalty + minCompPenalty, validComponents);
    }
}
