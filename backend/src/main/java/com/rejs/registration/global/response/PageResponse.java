package com.rejs.registration.global.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse <T>{
    private Integer totalPage;
    private Integer count;
    private Long totalElements;
    private Integer pageNumber;
    private Integer pageSize;
    private boolean hasNextPage;
    private List<T> data;

    public PageResponse(List<T> data, Integer count, Integer totalPage, Long totalElements, Integer pageNumber, Integer pageSize, boolean hasNextPage) {
        this.data = data;
        this.count = count;
        this.totalPage = totalPage;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasNextPage = hasNextPage;
    }

    public static <T> PageResponse<T> from(Page<T> page){
        return new PageResponse<>(page.getContent(), page.getNumberOfElements(), page.getTotalPages(), page.getTotalElements(), page.getNumber()+1, page.getSize(), page.hasNext());
    }

}
