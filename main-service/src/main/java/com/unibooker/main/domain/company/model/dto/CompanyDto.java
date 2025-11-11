package com.unibooker.main.domain.company.model.dto;

import com.unibooker.main.domain.company.model.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기업 DTO
 */
public class CompanyDto {

    /**
     * 기업 정보 조회 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String companyName;
        private String businessNumber;
        private String companySlug;
        private String logoUrl;
        private CompanyStatus status;
        private LocalDateTime approvedAt;
        private Long approvedBy;
        private String rejectionReason;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 기업 승인 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        private Long approvedBy;
    }

    /**
     * 기업 거절 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectionRequest {
        private String rejectionReason;
    }

    /**
     * 기업 정보 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String companyName;
        private String logoUrl;
    }
}