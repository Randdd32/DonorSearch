package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraDeviceTypeService extends AbstractInfraDictionaryService {
    public InfraDeviceTypeService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[ProductCatalogType] pct";
    }

    @Override
    protected String getIdColumn() {
        return "pct.[ID]";
    }

    @Override
    protected String getDisplayColumn() {
        return "COALESCE(NULLIF(LTRIM(RTRIM(pct.[Name])), ''), 'Без названия')";
    }

    @Override
    protected String getParentColumn() {
        return null;
    }

    @Override
    protected String getSortColumn() {
        return "pct.[Name]";
    }

    @Override
    protected String getAdditionalWhere() {
        return " AND pct.[Removed] = 0 AND pct.[ID] IN (:allowedTypes) ";
    }

    @Override
    protected void addAdditionalParameters(MapSqlParameterSource params) {
        params.addValue("allowedTypes", Constants.ALLOWED_PC_TYPE_UUIDS);
    }
}
