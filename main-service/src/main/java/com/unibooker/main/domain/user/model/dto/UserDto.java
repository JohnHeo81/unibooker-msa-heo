package com.unibooker.main.dto;

import com.unibooker.main.entity.Gender;
import com.unibooker.main.entity.UserRole;
import com.unibooker.main.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 DTO
 */
public class UserDto {

    /**
     * 사용자 정보 조회 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String email;
        private String name;
        private String phone;
        private String birthDate;
        private Gender gender;
        private UserRole role;
        private UserStatus status;
        private Long companyId;
        private Boolean isFirstLogin;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 사용자 정보 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String phone;
        private String birthDate;
        private Gender gender;
    }

    /**
     * 비밀번호 변경 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
    }
}