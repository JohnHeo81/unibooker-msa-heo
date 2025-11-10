package com.unibooker.resource.dto;

import com.unibooker.resource.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리소스 그룹 DTO
 */
public class ResourceGroupDto {

    /**
     * 리소스 그룹 생성 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private String groupCode;
        private String description;
        private String thumbnail;
        private ServiceCategory category;
        private Boolean isAlwaysAvailable;
        private Long companyId;
        private Long createdBy;
    }

    /**
     * 리소스 그룹 수정 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String groupCode;
        private String description;
        private String thumbnail;
        private ServiceCategory category;
        private Boolean isAlwaysAvailable;
        private Long updatedBy;
    }

    /**
     * 리소스 그룹 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String groupCode;
        private String description;
        private String thumbnail;
        private ServiceCategory category;
        private Boolean isAlwaysAvailable;
        private Boolean isActive;
        private Integer viewCount;
        private Long companyId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}