package com.github.randdd32.donor_search_backend.service.integration;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.web.dto.filter.InfraDeviceFilter;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InfraDeviceService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final IntegrationMappingService mappingService;

    public InfraDeviceService(
            @Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            IntegrationMappingService mappingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappingService = mappingService;
    }

    private static final String DEP_CTE = """
        WITH DepCTE AS (
            SELECT [Идентификатор], [Название], [ИД подразделения], CAST(LTRIM(RTRIM([Название])) AS NVARCHAR(MAX)) AS FullPath
            FROM dbo.[Подразделение]
            WHERE [ИД подразделения] IS NULL
            UNION ALL
            SELECT d.[Идентификатор], d.[Название], d.[ИД подразделения], c.FullPath + ' -> ' + LTRIM(RTRIM(d.[Название]))
            FROM dbo.[Подразделение] d
            INNER JOIN DepCTE c ON d.[ИД подразделения] = c.[Идентификатор]
        )
    """;

    private static final String BASE_DEVICE_SELECT = """
        SELECT 
            pc.[Идентификатор] AS id, 
            NULLIF(LTRIM(RTRIM(pc.[Название])), '') AS name, 
            NULLIF(LTRIM(RTRIM(pc.[Инвентарный номер])), '') AS inv_num, 
            NULLIF(LTRIM(RTRIM(pc.[SerialNumber])), '') AS serial_num,
            NULLIF(LTRIM(RTRIM(pc.[Примечание])), '') AS note,
            NULLIF(LTRIM(RTRIM(pc.[AssetTag])), '') AS asset_tag,
            NULLIF(LTRIM(RTRIM(pc.[Code])), '') AS code,
            NULLIF(LTRIM(RTRIM(pc.[Description])), '') AS description,
            too.[Идентификатор] AS model_id, 
            NULLIF(LTRIM(RTRIM(too.[Название])), '') AS model_name, 
            NULLIF(LTRIM(RTRIM(too.[ProductNumber])), '') AS model_product_number,
            NULLIF(LTRIM(RTRIM(too.[Note])), '') AS model_note,
            manuf.[Идентификатор] AS manuf_id, 
            NULLIF(LTRIM(RTRIM(manuf.[Название])), '') AS manuf_name, 
            pct.[ID] AS type_id, 
            NULLIF(LTRIM(RTRIM(pct.[Name])), '') AS type_name, 
            state.[ID] AS state_id, 
            NULLIF(LTRIM(RTRIM(state.[Name])), '') AS state_name,
            NULLIF(LTRIM(RTRIM(u.[FullName])), '') AS owner_name,
            NULLIF(LTRIM(RTRIM(u.[Телефон])), '') AS owner_phone,
            NULLIF(LTRIM(RTRIM(pos.[Название])), '') AS owner_position,
            dep.[Идентификатор] AS dept_id, 
            NULLIF(LTRIM(RTRIM(dep.FullPath)), '') AS dept_name,
            b.[Идентификатор] AS building_id, 
            f.[Идентификатор] AS floor_id, 
            r.[Идентификатор] AS room_id,
            COALESCE(NULLIF(LTRIM(RTRIM(b.[Название])), ''), 'Без здания') + ' -> ' + 
            COALESCE(NULLIF(LTRIM(RTRIM(f.[Название])), ''), 'Без этажа') + ' -> ' + 
            COALESCE(NULLIF(LTRIM(RTRIM(r.[Название])), ''), 'Без комнаты') + ' -> ' + 
            COALESCE(NULLIF(LTRIM(RTRIM(wp.[Название])), ''), 'Без РМ') AS location_path,
            a.[DateReceived] AS date_received, 
            a.[IsWorking] AS is_working,
            a.[Cost] AS cost,
            NULLIF(LTRIM(RTRIM(a.[UserField1])), '') AS pc_composition,
            NULLIF(LTRIM(RTRIM(a.[UserField3])), '') AS ownership_note,
            a.[DateInquiry] AS date_inquiry,
            a.[AppointmentDate] AS appointment_date,
            a.[DateAnnuled] AS date_annuled,
            NULLIF(LTRIM(RTRIM(org.[Название])), '') AS organization_name
    """;

    private static final String BASE_DEVICE_FROM_JOINS = """
        FROM dbo.[Оконечное оборудование] pc
        OUTER APPLY (
            SELECT TOP 1 * 
            FROM dbo.[Asset] a_inner 
            WHERE a_inner.[DeviceID] = pc.[Идентификатор] 
            ORDER BY a_inner.[DateInquiry] DESC, a_inner.[DateReceived] DESC
        ) a
        LEFT JOIN dbo.[LifeCycleState] state ON a.[LifeCycleStateID] = state.[ID]
        LEFT JOIN dbo.[Типы оконечного оборудования] too ON NULLIF(pc.[ИД типа ОО], 0) = too.[Идентификатор]
        LEFT JOIN dbo.[Производители] manuf ON NULLIF(too.[ИД производителя], 0) = manuf.[Идентификатор]
        LEFT JOIN dbo.[ProductCatalogType] pct ON too.[ProductCatalogTypeID] = pct.[ID]
        LEFT JOIN dbo.[Пользователи] u ON a.[UtilizerID] = u.[IMObjID] AND a.[UtilizerClassID] = 9
        LEFT JOIN dbo.[Должности] pos ON NULLIF(u.[ИД должности], 0) = pos.[Идентификатор]
        LEFT JOIN DepCTE dep ON dep.[Идентификатор] = CASE
            WHEN a.[UtilizerClassID] = 102 THEN a.[UtilizerID]
            WHEN a.[UtilizerClassID] = 9 THEN u.[ИД подразделения]
        END
        LEFT JOIN dbo.[Организация] org ON a.[UtilizerID] = org.[Идентификатор] AND a.[UtilizerClassID] = 101
        LEFT JOIN dbo.[Рабочее место] wp ON NULLIF(pc.[ИД рабочего места], 0) = wp.[Идентификатор]
        LEFT JOIN dbo.[Комната] r ON COALESCE(NULLIF(wp.[ИД комнаты], 0), NULLIF(pc.[ИД комнаты], 0)) = r.[Идентификатор]
        LEFT JOIN dbo.[Этаж] f ON NULLIF(r.[ИД этажа], 0) = f.[Идентификатор]
        LEFT JOIN dbo.[Здание] b ON NULLIF(f.[ИД здания], 0) = b.[Идентификатор]
    """;

    public PageDto<ExternalDeviceDto> getDevicesPage(InfraDeviceFilter filter, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        StringBuilder where = new StringBuilder(
                " WHERE pc.[Removed] = 0 " +
                        " AND pct.[ID] IN (:allowedTypes) " +
                        " AND (too.[Removed] = 0 OR too.[Removed] IS NULL) " +
                        " AND (pct.[Removed] = 0 OR pct.[Removed] IS NULL) "
        );
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("allowedTypes", Constants.ALLOWED_PC_TYPE_UUIDS);

        String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
        if (cleanSearch != null) {
            where.append(" AND (pc.[Название] LIKE '%' + :search + '%' ")
                    .append(" OR pc.[Инвентарный номер] LIKE '%' + :search + '%' ")
                    .append(" OR pc.[SerialNumber] LIKE '%' + :search + '%') ");
            params.addValue("search", cleanSearch);
        }

        addListFilter(where, params, "state.[ID]", "stateIds", filter.stateIds());
        addListFilter(where, params, "dep.[Идентификатор]", "departmentIds", filter.departmentIds());
        addListFilter(where, params, "manuf.[Идентификатор]", "manufacturerIds", filter.manufacturerIds());
        addListFilter(where, params, "pct.[ID]", "typeIds", filter.typeIds());
        addListFilter(where, params, "too.[Идентификатор]", "modelIds", filter.modelIds());
        addListFilter(where, params, "b.[Идентификатор]", "buildingIds", filter.buildingIds());
        addListFilter(where, params, "f.[Идентификатор]", "floorIds", filter.floorIds());
        addListFilter(where, params, "r.[Идентификатор]", "roomIds", filter.roomIds());

        if (filter.isWorking() != null) {
            where.append(" AND a.[IsWorking] = :isWorking ");
            params.addValue("isWorking", filter.isWorking() ? 1 : 0);
        }

        addDateRangeFilter(where, params, "a.[DateReceived]", "dateReceived", filter.dateReceivedFrom(), filter.dateReceivedTo());
        addDateRangeFilter(where, params, "a.[DateInquiry]", "dateInquiry", filter.dateInquiryFrom(), filter.dateInquiryTo());
        addDateRangeFilter(where, params, "a.[AppointmentDate]", "appointmentDate", filter.appointmentDateFrom(), filter.appointmentDateTo());

        if (filter.minCost() != null) {
            where.append(" AND a.[Cost] >= :minCost ");
            params.addValue("minCost", filter.minCost());
        }
        if (filter.maxCost() != null) {
            where.append(" AND a.[Cost] <= :maxCost ");
            params.addValue("maxCost", filter.maxCost());
        }

        String countSql = DEP_CTE + " SELECT COUNT(1) " + BASE_DEVICE_FROM_JOINS + where;
        Long totalCountObj = jdbcTemplate.queryForObject(countSql, params, Long.class);
        long totalCount = totalCountObj != null ? totalCountObj : 0L;
        if (totalCount == 0) {
            return PageDtoMapper.emptyPage(page, size);
        }

        String orderSql = " ORDER BY pc.[Идентификатор] DESC ";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            String direction = order.getDirection().name();

            String dbColumn = switch (order.getProperty()) {
                case "name" -> "pc.[Название]";
                case "inventoryNumber" -> "pc.[Инвентарный номер]";
                case "dateReceived" -> "a.[DateReceived]";
                case "state" -> "state.[Name]";
                default -> "pc.[Идентификатор]";
            };

            orderSql = " ORDER BY " + dbColumn + " " + direction + " ";
        }

        String fetchSql = DEP_CTE + " " + BASE_DEVICE_SELECT + " " +
                BASE_DEVICE_FROM_JOINS + where + orderSql +
                " OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY";

        params.addValue("offset", page * size);
        params.addValue("limit", size);

        List<ExternalDeviceDto> items = jdbcTemplate.query(fetchSql, params, this::mapDeviceRow);

        return PageDtoMapper.toDto(items, totalCount, page, size);
    }

    public ExternalDeviceDto getDeviceDetails(Long externalDeviceId) {
        String deviceSql = DEP_CTE + " " + BASE_DEVICE_SELECT + BASE_DEVICE_FROM_JOINS + """
             WHERE pc.[Идентификатор] = :id 
               AND pc.[Removed] = 0 
               AND pct.[ID] IN (:allowedTypes) 
               AND (too.[Removed] = 0 OR too.[Removed] IS NULL) 
               AND (pct.[Removed] = 0 OR pct.[Removed] IS NULL)
            """;

        List<ExternalDeviceDto> devices = jdbcTemplate.query(
                deviceSql,
                new MapSqlParameterSource()
                        .addValue("id", externalDeviceId)
                        .addValue("allowedTypes", Constants.ALLOWED_PC_TYPE_UUIDS),
                this::mapDeviceRow
        );
        if (devices.isEmpty()) {
            throw new NotFoundException(String.format("External device with id[%s] not found", externalDeviceId));
        }
        ExternalDeviceDto device = devices.get(0);

        String componentsSql = """
            SELECT 
                ad.[AdapterID] AS adapter_id,
                pct.[ID] AS category_id,
                NULLIF(LTRIM(RTRIM(ad.[Name])), '') AS external_name,
                NULLIF(LTRIM(RTRIM(pct.[Name])), '') AS category_name_ru,
                m.[Идентификатор] AS manufacturer_id,
                NULLIF(LTRIM(RTRIM(m.[Название])), '') AS manufacturer_name,
                NULLIF(LTRIM(RTRIM(ad.[SerialNo])), '') AS serial_number
            FROM dbo.[Adapter] ad
            JOIN dbo.[AdapterType] at ON ad.[AdapterTypeID] = at.[AdapterTypeID]
            JOIN dbo.[ProductCatalogType] pct ON at.[ProductCatalogTypeID] = pct.[ID]
            LEFT JOIN dbo.[Производители] m ON at.[VendorID] = m.[Идентификатор]
            WHERE ad.[TerminalDeviceID] = :id
        """;

        List<ExternalComponentDto> rawComponents = jdbcTemplate.query(
                componentsSql,
                new MapSqlParameterSource("id", externalDeviceId),
                (rs, rowNum) -> new ExternalComponentDto(
                        rs.getLong("adapter_id"),
                        rs.getLong("category_id"),
                        rs.getString("external_name"),
                        ExternalComponentCategory.fromInfraName(rs.getString("category_name_ru")),
                        rs.getLong("manufacturer_id"),
                        rs.getString("manufacturer_name"),
                        rs.getString("serial_number"),
                        null
                )
        );

        if (rawComponents.isEmpty()) {
            return device;
        }

        List<String> extNamesToFetch = rawComponents.stream()
                .map(ExternalComponentDto::externalName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();

        Map<String, Long> mappedIdsCache = mappingService.getMappedComponentIds(extNamesToFetch);

        List<ExternalComponentDto> enrichedComponents = rawComponents.stream().map(comp -> {
            Long mappedId = null;
            if (comp.externalName() != null) {
                mappedId = mappedIdsCache.get(comp.externalName().toLowerCase());
            }

            return new ExternalComponentDto(
                    comp.adapterId(),
                    comp.categoryId(),
                    comp.externalName(),
                    comp.category(),
                    comp.manufacturerId(),
                    comp.manufacturerName(),
                    comp.serialNumber(),
                    mappedId
            );
        }).toList();

        device.components().addAll(enrichedComponents);
        return device;
    }

    public List<ExternalDeviceDto> getPotentialDonors(Long excludeDeviceId, ExternalComponentCategory category) {
        List<String> infraNames = category.getInfraNames();
        if (infraNames.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = DEP_CTE + " " + BASE_DEVICE_SELECT + """
            ,
            ad.[AdapterID] AS adapter_id,
            pct_comp.[ID] AS category_id,
            NULLIF(LTRIM(RTRIM(ad.[Name])), '') AS external_name,
            NULLIF(LTRIM(RTRIM(pct_comp.[Name])), '') AS category_name_ru,
            comp_manuf.[Идентификатор] AS comp_manufacturer_id,
            NULLIF(LTRIM(RTRIM(comp_manuf.[Название])), '') AS comp_manufacturer_name,
            NULLIF(LTRIM(RTRIM(ad.[SerialNo])), '') AS comp_serial_number
            """ + BASE_DEVICE_FROM_JOINS + """
            JOIN dbo.[Adapter] ad ON pc.[Идентификатор] = ad.[TerminalDeviceID]
            JOIN dbo.[AdapterType] at ON ad.[AdapterTypeID] = at.[AdapterTypeID]
            JOIN dbo.[ProductCatalogType] pct_comp ON at.[ProductCatalogTypeID] = pct_comp.[ID]
            LEFT JOIN dbo.[Производители] comp_manuf ON at.[VendorID] = comp_manuf.[Идентификатор]
            
            WHERE pc.[Идентификатор] != :excludeId
              AND pc.[Removed] = 0
              AND pct.[ID] IN (:allowedTypes)
              AND (too.[Removed] = 0 OR too.[Removed] IS NULL)
              AND (pct.[Removed] = 0 OR pct.[Removed] IS NULL)
              AND pct_comp.[Name] IN (:categoryNames)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("excludeId", excludeDeviceId)
                .addValue("allowedTypes", Constants.ALLOWED_PC_TYPE_UUIDS)
                .addValue("categoryNames", infraNames);

        List<ExternalDeviceDto> devices = jdbcTemplate.query(sql, params, rs -> {
            Map<Long, ExternalDeviceDto> deviceMap = new LinkedHashMap<>();

            while (rs.next()) {
                Long deviceId = rs.getLong("id");

                ExternalDeviceDto device = deviceMap.computeIfAbsent(deviceId, id -> {
                    try {
                        return mapDeviceRow(rs, 0);
                    } catch (java.sql.SQLException e) {
                        throw new RuntimeException("Ошибка маппинга базовой строки устройства", e);
                    }
                });

                ExternalComponentDto component = new ExternalComponentDto(
                        rs.getLong("adapter_id"),
                        rs.getLong("category_id"),
                        rs.getString("external_name"),
                        ExternalComponentCategory.fromInfraName(rs.getString("category_name_ru")),
                        rs.getLong("comp_manufacturer_id"),
                        rs.getString("comp_manufacturer_name"),
                        rs.getString("comp_serial_number"),
                        null
                );

                device.components().add(component);
            }
            return new ArrayList<>(deviceMap.values());
        });

        if (devices == null || devices.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> extNamesToFetch = devices.stream()
                .flatMap(device -> device.components().stream())
                .map(ExternalComponentDto::externalName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();

        if (!extNamesToFetch.isEmpty()) {
            Map<String, Long> mappedIdsCache = mappingService.getMappedComponentIds(extNamesToFetch);

            for (ExternalDeviceDto device : devices) {
                List<ExternalComponentDto> enrichedComponents = device.components().stream().map(c -> {
                    Long mappedId = c.externalName() != null ? mappedIdsCache.get(c.externalName().toLowerCase()) : null;
                    return new ExternalComponentDto(
                            c.adapterId(), c.categoryId(), c.externalName(), c.category(),
                            c.manufacturerId(),c.manufacturerName(), c.serialNumber(), mappedId
                    );
                }).toList();

                device.components().clear();
                device.components().addAll(enrichedComponents);
            }
        }

        return devices;
    }

    private ExternalDeviceDto mapDeviceRow(ResultSet rs, int rowNum) throws SQLException {
        return new ExternalDeviceDto(
                rs.getLong("id"),
                rs.getString("name") != null ? rs.getString("name") : "Без названия",
                rs.getString("inv_num"),
                rs.getString("serial_num"),
                rs.getString("note"),
                rs.getString("asset_tag"),
                rs.getString("code"),
                rs.getString("description"),
                rs.getLong("model_id"),
                rs.getString("model_name") != null ? rs.getString("model_name") : "Неизвестно",
                rs.getString("model_product_number"),
                rs.getString("model_note"),
                rs.getLong("manuf_id"),
                rs.getString("manuf_name"),
                rs.getString("type_id"),
                rs.getString("type_name"),
                rs.getString("state_id"),
                ExternalDeviceState.fromInfraName(rs.getString("state_name")),
                rs.getString("owner_name") != null ? rs.getString("owner_name") : "Неизвестно",
                rs.getString("owner_phone"),
                rs.getString("owner_position"),
                rs.getString("dept_id"),
                rs.getString("dept_name") != null ? rs.getString("dept_name") : "Без отдела",
                rs.getLong("building_id"),
                rs.getLong("floor_id"),
                rs.getLong("room_id"),
                rs.getString("location_path"),
                rs.getTimestamp("date_received") != null ? rs.getTimestamp("date_received").toInstant() : null,
                rs.getObject("is_working") != null ? rs.getBoolean("is_working") : null,
                rs.getBigDecimal("cost"),
                rs.getString("pc_composition"),
                rs.getString("ownership_note"),
                rs.getTimestamp("date_inquiry") != null ? rs.getTimestamp("date_inquiry").toInstant() : null,
                rs.getTimestamp("appointment_date") != null ? rs.getTimestamp("appointment_date").toInstant() : null,
                rs.getTimestamp("date_annuled") != null ? rs.getTimestamp("date_annuled").toInstant() : null,
                rs.getString("organization_name"),
                new ArrayList<>()
        );
    }

    private void addListFilter(StringBuilder where, MapSqlParameterSource params, String column, String paramName, List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            where.append(" AND ").append(column).append(" IN (:").append(paramName).append(") ");
            params.addValue(paramName, values);
        }
    }

    private void addDateRangeFilter(StringBuilder where, MapSqlParameterSource params, String column,
                                    String paramPrefix, Instant from, Instant to) {
        if (from != null) {
            where.append(" AND ").append(column).append(" >= :").append(paramPrefix).append("From ");
            params.addValue(paramPrefix + "From", java.sql.Timestamp.from(from));
        }
        if (to != null) {
            where.append(" AND ").append(column).append(" <= :").append(paramPrefix).append("To ");
            params.addValue(paramPrefix + "To", java.sql.Timestamp.from(to));
        }
    }
}
