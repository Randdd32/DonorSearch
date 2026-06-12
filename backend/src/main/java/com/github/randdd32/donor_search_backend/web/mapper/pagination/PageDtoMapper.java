package com.github.randdd32.donor_search_backend.web.mapper.pagination;

import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class PageDtoMapper {
    public static <D, E> PageDto<D> toDto(Page<E> page, Function<E, D> mapper) {
        return new PageDto<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumberOfElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public static <D> PageDto<D> toDto(List<D> items, long totalCount, int page, int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalCount / size) : 0;
        return new PageDto<>(
                items,
                items.size(),
                page,
                size,
                totalPages,
                totalCount,
                page == 0,
                page >= totalPages - 1,
                page < totalPages - 1,
                page > 0
        );
    }

    public static <D> PageDto<D> emptyPage(int page, int size) {
        return new PageDto<>(
                Collections.emptyList(),
                0,
                page,
                size,
                0,
                0L,
                true,
                true,
                false,
                false
        );
    }

    private PageDtoMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
