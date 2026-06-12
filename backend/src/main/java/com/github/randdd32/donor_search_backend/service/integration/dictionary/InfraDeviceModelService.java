package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraDeviceModelService extends AbstractInfraDictionaryService {
    public InfraDeviceModelService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Типы оконечного оборудования] t LEFT JOIN dbo.[Производители] m ON NULLIF(t.[ИД производителя], 0) = m.[Идентификатор]";
    }

    @Override
    protected String getIdColumn() {
        return "t.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "COALESCE(NULLIF(LTRIM(RTRIM(t.[Название])), ''), 'Без названия') + " +
                "CASE WHEN NULLIF(LTRIM(RTRIM(m.[Название])), '') IS NOT NULL " +
                "THEN ' (' + LTRIM(RTRIM(m.[Название])) + ')' ELSE '' END";
    }

    @Override
    protected String getParentColumn() {
        return "t.[ИД производителя]";
    }

    @Override
    protected String getSortColumn() {
        return "t.[Название]";
    }

    @Override
    protected String getAdditionalWhere() {
        return " AND t.[Removed] = 0 AND t.[ProductCatalogTypeID] IN (:allowedTypes) ";
    }

    @Override
    protected void addAdditionalParameters(MapSqlParameterSource params) {
        params.addValue("allowedTypes", Constants.ALLOWED_PC_TYPE_UUIDS);
    }

    @Override
    protected String getSearchCondition() {
        return " AND (t.[Название] LIKE '%' + :search + '%' OR m.[Название] LIKE '%' + :search + '%') ";
    }
}
