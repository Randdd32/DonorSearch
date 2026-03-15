package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraManufacturerService extends AbstractInfraDictionaryService {
    public InfraManufacturerService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Производители] m";
    }

    @Override
    protected String getIdColumn() {
        return "m.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "m.[Название]";
    }

    @Override
    protected String getParentColumn() {
        return null;
    }

    @Override
    protected String getSortColumn() {
        return "m.[Название]";
    }
}
