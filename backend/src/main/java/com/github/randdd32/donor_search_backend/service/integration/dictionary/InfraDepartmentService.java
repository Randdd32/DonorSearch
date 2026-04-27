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
    protected String getCteSql() {
        return """
            WITH DepCTE AS (
                SELECT [Идентификатор], [Название], [ИД подразделения], 
                       CAST(LTRIM(RTRIM([Название])) AS NVARCHAR(MAX)) AS FullPath
                FROM dbo.[Подразделение]
                WHERE [ИД подразделения] IS NULL
                UNION ALL
                SELECT d.[Идентификатор], d.[Название], d.[ИД подразделения], 
                       c.FullPath + ' -> ' + LTRIM(RTRIM(d.[Название]))
                FROM dbo.[Подразделение] d
                INNER JOIN DepCTE c ON d.[ИД подразделения] = c.[Идентификатор]
            )
        """;
    }

    @Override
    protected String getBaseSql() {
        return "FROM DepCTE dep";
    }

    @Override
    protected String getIdColumn() {
        return "dep.[Идентификатор]";
    }

    @Override
    protected String getDisplayColumn() {
        return "COALESCE(NULLIF(LTRIM(RTRIM(dep.FullPath)), ''), 'Без названия')";
    }

    @Override
    protected String getParentColumn() {
        return "dep.[ИД подразделения]";
    }

    @Override
    protected String getSortColumn() {
        return "dep.FullPath";
    }
}
