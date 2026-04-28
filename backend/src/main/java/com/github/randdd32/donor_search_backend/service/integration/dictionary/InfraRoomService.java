package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraRoomService extends AbstractInfraDictionaryService {
    public InfraRoomService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return """
            FROM dbo.[Комната] r 
            LEFT JOIN dbo.[Этаж] f ON NULLIF(r.[ИД этажа], 0) = f.[Идентификатор] 
            LEFT JOIN dbo.[Здание] z ON NULLIF(f.[ИД здания], 0) = z.[Идентификатор]
            LEFT JOIN dbo.[Типы комнат] rt ON NULLIF(r.[ИД типа], 0) = rt.[Идентификатор]
        """;
    }

    @Override
    protected String getIdColumn() {
        return "r.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return """
            '"' + COALESCE(NULLIF(LTRIM(RTRIM(r.[Название])), ''), 'Без названия') + '"' + 
            ' (' + COALESCE(NULLIF(LTRIM(RTRIM(f.[Название])), ''), 'Без этажа') + ', ' + 
            COALESCE(NULLIF(LTRIM(RTRIM(z.[Название])), ''), 'Без здания') + ')' + 
            COALESCE(' - ' + NULLIF(LTRIM(RTRIM(rt.[Название])), ''), '')
        """;
    }

    @Override
    protected String getParentColumn() {
        return "r.[ИД этажа]";
    }

    @Override
    protected String getSortColumn() {
        return "r.[Название]";
    }
}
