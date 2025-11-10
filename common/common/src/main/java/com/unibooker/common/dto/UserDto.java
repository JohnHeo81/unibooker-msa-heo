package com.unibooker.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 DTO (서비스 간 통신용)
 * - 최소한의 정보만 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 이메일
     */
    private String email;

    /**
     * 이름
     */
    private String name;

    /**
     * 권한 (USER, ADMIN, MANAGER, SUPER)
     */
    private String role;

    /**
     * 기업 ID (nullable)
     */
    private Long companyId;
}