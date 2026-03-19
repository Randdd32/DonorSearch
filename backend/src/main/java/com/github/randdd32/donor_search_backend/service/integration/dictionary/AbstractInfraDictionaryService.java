package com.github.randdd32.donor_search_backend.service.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.NamedDictionaryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public abstract class AbstractInfraDictionaryService {
    protected final NamedParameterJdbcTemplate jdbcTemplate;

    protected AbstractInfraDictionaryService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getBaseSql();
    protected abstract String getIdColumn();
    protected abstract String getDisplayColumn();
    protected abstract String getParentColumn();
    protected abstract String getSortColumn();

    public PageDto<NamedDictionaryDto> search(String search, List<Long> parentIds, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        String cleanSearch = QueryUtils.cleanSearchToken(search);
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (cleanSearch != null) {
            where.append(" AND LOWER(")
                    .append(getDisplayColumn())
                    .append(") LIKE '%' + :search + '%' ");
            params.addValue("search", cleanSearch);
        }

        if (!CollectionUtils.isEmpty(parentIds) && getParentColumn() != null) {
            where.append(" AND ").append(getParentColumn()).append(" IN (:parentIds) ");
            params.addValue("parentIds", parentIds);
        }

        String countSql = "SELECT COUNT(1) " + getBaseSql() + " " + where;
        Long totalCountObj = jdbcTemplate.queryForObject(countSql, params, Long.class);
        long totalCount = totalCountObj != null ? totalCountObj : 0L;
        if (totalCount == 0) {
            return PageDtoMapper.emptyPage(page, size);
        }

        params.addValue("offset", page * size);
        params.addValue("limit", size);

        String fetchSql = String.format(
                "SELECT %s AS id, %s AS name %s %s ORDER BY %s ASC OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
                getIdColumn(), getDisplayColumn(), getBaseSql(), where, getSortColumn()
        );

        List<NamedDictionaryDto> items = jdbcTemplate.query(fetchSql, params,
                (rs, rowNum) -> new NamedDictionaryDto(rs.getLong("id"), rs.getString("name")));

        return PageDtoMapper.toDto(items, totalCount, page, size);
    }

    public List<NamedDictionaryDto> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        String sql = String.format(
                "SELECT %s AS id, %s AS name %s WHERE %s IN (:ids)",
                getIdColumn(), getDisplayColumn(), getBaseSql(), getIdColumn()
        );

        return jdbcTemplate.query(sql, new MapSqlParameterSource("ids", ids),
                (rs, rowNum) -> new NamedDictionaryDto(rs.getLong("id"), rs.getString("name")));
    }
}
