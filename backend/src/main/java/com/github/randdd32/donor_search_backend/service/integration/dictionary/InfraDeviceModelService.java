package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InfraDeviceModelService extends AbstractInfraDictionaryService {
    public InfraDeviceModelService(@Qualifier("msSqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getBaseSql() {
        return "FROM dbo.[Типы оконечного оборудования] t LEFT JOIN dbo.[Производители] m ON t.[ИД производителя] = m.[Идентификатор]";
    }

    @Override
    protected String getIdColumn() {
        return "t.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "t.[Название] + ISNULL(' (' + m.[Название] + ')', '')";
    }

    @Override
    protected String getParentColumn() {
        return "t.[ИД производителя]";
    }

    @Override
    protected String getSortColumn() {
        return "t.[Название]";
    }
}
