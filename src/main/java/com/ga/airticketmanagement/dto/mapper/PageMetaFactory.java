package com.ga.airticketmanagement.dto.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ga.airticketmanagement.dto.response.PageMeta;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PageMetaFactory {

    public static PageMeta from(Page<?> page){

        Integer nextPage = page.hasNext() ? page.getNumber() + 2 : null;
        Integer prevPage = page.hasPrevious() ? page.getNumber() : null;

        return new PageMeta(
         page.getNumber() + 1,
         page.getSize(),
         page.getTotalElements(),
         page.getTotalPages(),
         nextPage,
         prevPage
        );
    }
}
