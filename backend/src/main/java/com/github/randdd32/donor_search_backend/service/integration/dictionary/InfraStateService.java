package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraStateService extends AbstractInfraDictionaryService {
    public InfraStateService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[LifeCycleState] s";
    }

    @Override
    protected String getIdColumn() {
        return "s.[ID]";
    }

    @Override
    protected String getDisplayColumn() {
        return "s.[Name]";
    }

    @Override
    protected String getParentColumn() {
        return null;
    }

    @Override
    protected String getSortColumn() {
        return "s.[Name]";
    }
}
