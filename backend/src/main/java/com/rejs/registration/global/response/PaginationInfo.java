package com.rejs.registration.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
public class PaginationInfo {
    private Integer count;
    private Integer requestNumber;
    private Integer requestSize;
    private Boolean hasNextPage;
    private Integer totalPage;
    private Long totalElements;
    private Integer blockLeft;
    private Integer blockRight;

    private static int BLOCK_SIZE = 10;

    public static PaginationInfo of(Page<?> page){
        int blockLeft = (page.getNumber()/BLOCK_SIZE) * BLOCK_SIZE + 1;
        int blockRight = Math.min(blockLeft + BLOCK_SIZE - 1, page.getTotalPages());

        return PaginationInfo.builder()
                .count(page.getNumberOfElements())
                .requestNumber(page.getNumber()+1)
                .requestSize(page.getSize())
                .hasNextPage(page.hasNext())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .blockLeft(blockLeft)
                .blockRight(blockRight)
                .build();
    }
}
