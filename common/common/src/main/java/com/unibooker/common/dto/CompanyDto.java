package com.unibooker.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기업 정보 DTO (서비스 간 통신용)
 * - 최소한의 정보만 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    /**
     * 기업 ID
     */
    private Long companyId;

    /**
     * 기업명
     */
    private String name;

    /**
     * Company Slug (URL 식별자)
     */
    private String slug;

    /**
     * 기업 상태 (PENDING, ACTIVE, SUSPENDED, REJECTED)
     */
    private String status;
}