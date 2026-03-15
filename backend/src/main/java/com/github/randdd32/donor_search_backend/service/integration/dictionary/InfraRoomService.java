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
        return "FROM dbo.[Комната] r JOIN dbo.[Этаж] f ON r.[ИД этажа] = f.[Идентификатор] JOIN dbo.[Здание] z ON f.[ИД здания] = z.[Идентификатор]";
    }

    @Override
    protected String getIdColumn() {
        return "r.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "r.[Название] + ' (' + f.[Название] + ', ' + z.[Название] + ')'";
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
