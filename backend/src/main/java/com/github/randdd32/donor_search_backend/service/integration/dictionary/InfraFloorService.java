package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraFloorService extends AbstractInfraDictionaryService {
    public InfraFloorService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Этаж] f JOIN dbo.[Здание] z ON f.[ИД здания] = z.[Идентификатор]";
    }

    @Override
    protected String getIdColumn() {
        return "f.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "f.[Название] + ' (' + z.[Название] + ')'";
    }

    @Override
    protected String getParentColumn() {
        return "f.[ИД здания]";
    }

    @Override
    protected String getSortColumn() {
        return "f.[Название]";
    }
}
