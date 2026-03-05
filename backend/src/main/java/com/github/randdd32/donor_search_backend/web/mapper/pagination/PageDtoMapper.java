package com.github.randdd32.donor_search_backend.web.mapper.pagination;

import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import org.springframework.data.domain.Page;

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

    private PageDtoMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
