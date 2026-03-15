package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraBuildingService extends AbstractInfraDictionaryService {
    public InfraBuildingService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Здание] z";
    }

    @Override
    protected String getIdColumn() {
        return "z.[Идентификатор]";
    }

    @Override protected String getDisplayColumn() {
        return "z.[Название]";
    }

    @Override
    protected String getParentColumn() {
        return null;
    }

    @Override
    protected String getSortColumn() {
        return "z.[Название]";
    }
}
