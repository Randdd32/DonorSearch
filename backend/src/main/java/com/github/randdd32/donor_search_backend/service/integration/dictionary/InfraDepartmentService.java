package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraDepartmentService extends AbstractInfraDictionaryService {
    public InfraDepartmentService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Подразделение] d";
    }

    @Override
    protected String getIdColumn() {
        return "d.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "d.[Название]";
    }

    @Override
    protected String getParentColumn() {
        return null;
    }

    @Override
    protected String getSortColumn() {
        return "d.[Название]";
    }
}

