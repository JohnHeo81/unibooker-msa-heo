package com.unibooker.resource.dto;

import com.unibooker.resource.entity.ResourceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 리소스 DTO
 */
public class ResourceDto {

    /**
     * 리소스 생성 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private String description;
        private String resourceImage;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer timeInterval;
        private Integer capacity;
        private Integer row;
        private Integer col;
        private Long resourceGroupId;
        private Long createdBy;
    }

    /**
     * 리소스 수정 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String description;
        private String resourceImage;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer capacity;
        private Integer row;
        private Integer col;
        private Long updatedBy;
    }

    /**
     * 리소스 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String resourceImage;
        private Boolean isActive;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer timeInterval;
        private Integer capacity;
        private Integer row;
        private Integer col;
        private ResourceStatus status;
        private Long resourceGroupId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
