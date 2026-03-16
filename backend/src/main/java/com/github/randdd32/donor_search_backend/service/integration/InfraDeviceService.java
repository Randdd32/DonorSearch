package com.github.randdd32.donor_search_backend.service.integration;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
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

    private static final String BASE_DEVICE_SELECT = """
        SELECT 
            pc.[Идентификатор] AS id, pc.[Название] AS name, pc.[Инвентарный номер] AS inv_num, pc.[SerialNumber] AS serial_num,
            too.[Название] AS model_name, manuf.[Название] AS manuf_name, pct.[Name] AS type_name, state.[Name] AS state_name,
            LTRIM(RTRIM(ISNULL(u.[Фамилия], '') + ' ' + ISNULL(u.[Имя], '') + ' ' + ISNULL(u.[Отчество], ''))) AS owner_name,
            ISNULL(dep.[Название], 'Без отдела') AS dept_name,
            ISNULL(b.[Название], 'Без здания') + ' -> ' + ISNULL(f.[Название], 'Без этажа') + ' -> ' + 
            ISNULL(r.[Название], 'Без комнаты') + ' -> ' + ISNULL(wp.[Название], 'Без РМ') AS location_path,
            a.[DateReceived] AS date_received, a.[IsWorking] AS is_working
    """;

    private static final String BASE_DEVICE_FROM_JOINS = """
        FROM dbo.[Оконечное оборудование] pc
        JOIN dbo.[Asset] a ON pc.[Идентификатор] = a.[DeviceID]
        JOIN dbo.[LifeCycleState] state ON a.[LifeCycleStateID] = state.[ID]
        LEFT JOIN dbo.[Типы оконечного оборудования] too ON pc.[ИД типа ОО] = too.[Идентификатор]
        LEFT JOIN dbo.[Производители] manuf ON too.[ИД производителя] = manuf.[Идентификатор]
        LEFT JOIN dbo.[ProductCatalogType] pct ON too.[ProductCatalogTypeID] = pct.[ID]
        LEFT JOIN dbo.[Пользователи] u ON a.[UtilizerID] = u.[IMObjID] AND a.[UtilizerClassID] = 9
        LEFT JOIN dbo.[Подразделение] dep ON dep.[Идентификатор] = CASE
            WHEN a.[UtilizerClassID] = 102 THEN a.[UtilizerID]         
            WHEN a.[UtilizerClassID] = 9 THEN u.[ИД подразделения]     
        END
        LEFT JOIN dbo.[Рабочее место] wp ON pc.[ИД рабочего места] = wp.[Идентификатор]
        LEFT JOIN dbo.[Комната] r ON COALESCE(wp.[ИД комнаты], pc.[ИД комнаты]) = r.[Идентификатор]
        LEFT JOIN dbo.[Этаж] f ON r.[ИД этажа] = f.[Идентификатор]
        LEFT JOIN dbo.[Здание] b ON f.[ИД здания] = b.[Идентификатор]
    """;

    public PageDto<ExternalDeviceDto> getDevicesPage(
            String search, List<Long> stateIds, List<Long> departmentIds,
            List<Long> manufacturerIds, List<Long> typeIds, List<Long> modelIds,
            List<Long> buildingIds, List<Long> floorIds, List<Long> roomIds,
            Instant dateReceivedFrom, Instant dateReceivedTo, Boolean isWorking,
            Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        String cleanSearch = QueryUtils.cleanSearchToken(search);
        if (cleanSearch != null) {
            where.append(" AND (LOWER(pc.[Название]) LIKE '%' + :search + '%' ")
                    .append(" OR LOWER(pc.[Инвентарный номер]) LIKE '%' + :search + '%' ")
                    .append(" OR LOWER(pc.[SerialNumber]) LIKE '%' + :search + '%') ");
            params.addValue("search", cleanSearch);
        }

        if (!CollectionUtils.isEmpty(stateIds)) {
            where.append(" AND state.[ID] IN (:stateIds) ");
            params.addValue("stateIds", stateIds);
        }
        if (!CollectionUtils.isEmpty(departmentIds)) {
            where.append(" AND dep.[Идентификатор] IN (:departmentIds) ");
            params.addValue("departmentIds", departmentIds);
        }
        if (!CollectionUtils.isEmpty(manufacturerIds)) {
            where.append(" AND manuf.[Идентификатор] IN (:manufacturerIds) ");
            params.addValue("manufacturerIds", manufacturerIds);
        }
        if (!CollectionUtils.isEmpty(typeIds)) {
            where.append(" AND pct.[ID] IN (:typeIds) ");
            params.addValue("typeIds", typeIds);
        }
        if (!CollectionUtils.isEmpty(modelIds)) {
            where.append(" AND too.[Идентификатор] IN (:modelIds) ");
            params.addValue("modelIds", modelIds);
        }
        if (!CollectionUtils.isEmpty(buildingIds)) {
            where.append(" AND b.[Идентификатор] IN (:buildingIds) ");
            params.addValue("buildingIds", buildingIds);
        }
        if (!CollectionUtils.isEmpty(floorIds)) {
            where.append(" AND f.[Идентификатор] IN (:floorIds) ");
            params.addValue("floorIds", floorIds);
        }
        if (!CollectionUtils.isEmpty(roomIds)) {
            where.append(" AND r.[Идентификатор] IN (:roomIds) ");
            params.addValue("roomIds", roomIds);
        }

        if (isWorking != null) {
            where.append(" AND a.[IsWorking] = :isWorking ");
            params.addValue("isWorking", isWorking ? 1 : 0);
        }
        if (dateReceivedFrom != null) {
            where.append(" AND a.[DateReceived] >= :dateFrom ");
            params.addValue("dateFrom", java.sql.Timestamp.from(dateReceivedFrom));
        }
        if (dateReceivedTo != null) {
            where.append(" AND a.[DateReceived] <= :dateTo ");
            params.addValue("dateTo", java.sql.Timestamp.from(dateReceivedTo));
        }

        String countSql = "SELECT COUNT(1) " + BASE_DEVICE_FROM_JOINS + where;
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
                case "id" -> "pc.[Идентификатор]";
                case "name" -> "pc.[Название]";
                case "inventoryNumber" -> "pc.[Инвентарный номер]";
                case "dateReceived" -> "a.[DateReceived]";
                case "state" -> "state.[Name]";
                default -> "pc.[Идентификатор]";
            };

            orderSql = " ORDER BY " + dbColumn + " " + direction + " ";
        }

        String fetchSql = BASE_DEVICE_SELECT + " " +
                BASE_DEVICE_FROM_JOINS + where + orderSql +
                " OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY";

        params.addValue("offset", page * size);
        params.addValue("limit", size);

        List<ExternalDeviceDto> items = jdbcTemplate.query(fetchSql, params, this::mapDeviceRow);

        return PageDtoMapper.toDto(items, totalCount, page, size);
    }

    public ExternalDeviceDto getDeviceDetails(Long externalDeviceId) {
        String deviceSql = BASE_DEVICE_SELECT + BASE_DEVICE_FROM_JOINS + " WHERE pc.[Идентификатор] = :id";

        List<ExternalDeviceDto> devices = jdbcTemplate.query(
                deviceSql,
                new MapSqlParameterSource("id", externalDeviceId),
                this::mapDeviceRow
        );
        if (devices.isEmpty()) {
            throw new NotFoundException(String.format("External device with id [%s] not found", externalDeviceId));
        }
        ExternalDeviceDto device = devices.get(0);

        String componentsSql = """
            SELECT 
                ad.[AdapterID] AS adapter_id,
                pct.[ID] AS category_id,
                ad.[Name] AS external_name,
                pct.[Name] AS category_name_ru,
                m.[Название] AS manufacturer_name,
                ad.[SerialNo] AS serial_number
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
                    comp.manufacturerName(),
                    comp.serialNumber(),
                    mappedId
            );
        }).toList();

        device.components().addAll(enrichedComponents);
        return device;
    }

    private ExternalDeviceDto mapDeviceRow(ResultSet rs, int rowNum) throws SQLException {
        return new ExternalDeviceDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("inv_num"),
                rs.getString("serial_num"),
                rs.getString("model_name"),
                rs.getString("manuf_name"),
                rs.getString("type_name"),
                rs.getString("state_name"),
                rs.getString("owner_name").isBlank() ? "Неизвестно" : rs.getString("owner_name"),
                rs.getString("dept_name"),
                rs.getString("location_path"),
                rs.getTimestamp("date_received") != null ? rs.getTimestamp("date_received").toInstant() : null,
                rs.getBoolean("is_working"),
                new ArrayList<>()
        );
    }
}
