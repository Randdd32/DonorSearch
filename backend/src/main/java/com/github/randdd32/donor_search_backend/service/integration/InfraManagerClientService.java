package com.github.randdd32.donor_search_backend.service.integration;

import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ Service
@RequiredArgsConstructor
public class InfraManagerClientService {
    private final NamedParameterJdbcTemplate msSqlJdbcTemplate;

    public List<ExternalDeviceDto> getPotentialDonorDevices() {
        String devicesSql = """
            SELECT 
                pc.[Идентификатор] AS pc_id,
                pc.[Название] AS pc_name,
                pc.[Инвентарный номер] AS inv_num,
                pc.[SerialNumber] AS serial_num,
                state.[Name] AS state_name,
                COALESCE(u.[Фамилия] + ' ' + u.[Имя], 'Неизвестно') AS owner_name,
                COALESCE(dep.[Название], 'Без отдела') AS department_name,
                COALESCE(b.[Название] + ' -> ' + f.[Название] + ' -> ' + r.[Название], 'Неизвестно') AS location_path
            FROM dbo.[Оконечное оборудование] pc
            JOIN dbo.[Asset] a ON pc.[Идентификатор] = a.[DeviceID]
            JOIN dbo.[LifeCycleState] state ON a.[LifeCycleStateID] = state.[ID]
            LEFT JOIN dbo.[Пользователи] u ON a.[UtilizerID] = u.[Идентификатор] AND a.[UtilizerClassID] = 9
            LEFT JOIN dbo.[Подразделение] dep ON a.[UtilizerID] = dep.[Идентификатор] AND a.[UtilizerClassID] = 102
            LEFT JOIN dbo.[Комната] r ON pc.[ИД комнаты] = r.[Идентификатор]
            LEFT JOIN dbo.[Этаж] f ON r.[ИД этажа] = f.[Идентификатор]
            LEFT JOIN dbo.[Здание] b ON f.[ИД здания] = b.[Идентификатор]
            WHERE state.[Name] IN ('На складе', 'Списано', 'В резерве', 'Ремонт')
        """;

        List<ExternalDeviceDto> devices = msSqlJdbcTemplate.query(devicesSql, (rs, rowNum) -> {
            return new ExternalDeviceDto(
                    rs.getLong("pc_id"),
                    rs.getString("pc_name"),
                    rs.getString("inv_num"),
                    rs.getString("serial_num"),
                    rs.getString("state_name"),
                    rs.getString("owner_name"),
                    rs.getString("department_name"),
                    rs.getString("location_path"),
                    new java.util.ArrayList<>()
            );
        });

        if (devices.isEmpty()) {
            return devices;
        }

        List<Long> pcIds = devices.stream().map(ExternalDeviceDto::externalId).toList();

        String componentsSql = """
            SELECT 
                ad.[AdapterID] AS adapter_id,
                ad.[TerminalDeviceID] AS pc_id,
                ad.[Name] AS external_name,
                cat.[Name] AS category_name
            FROM dbo.[Adapter] ad
            JOIN dbo.[AdapterType] at ON ad.[AdapterTypeID] = at.[AdapterTypeID]
            JOIN dbo.[ProductCatalogType] cat ON at.[ProductCatalogTypeID] = cat.[ID]
            WHERE ad.[TerminalDeviceID] IN (:pcIds)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource("pcIds", pcIds);

        List<ExternalComponentDto> allComponents = msSqlJdbcTemplate.query(componentsSql, params, (rs, rowNum) -> {
            return new ExternalComponentDto(
                    rs.getLong("adapter_id"),
                    rs.getLong("pc_id"),
                    rs.getString("external_name"),
                    rs.getString("category_name")
            );
        });

        Map<Long, List<ExternalComponentDto>> componentsByPc = allComponents.stream()
                .collect(Collectors.groupingBy(ExternalComponentDto::pcId));

        devices.forEach(device -> {
            List<ExternalComponentDto> pcComponents = componentsByPc.get(device.externalId());
            if (pcComponents != null) {
                device.components().addAll(pcComponents);
            }
        });

        return devices;
    }
}
