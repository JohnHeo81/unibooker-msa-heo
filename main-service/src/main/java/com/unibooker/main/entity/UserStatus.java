package com.unibooker.main.entity;

/**
 * 사용자 상태
 */
public enum UserStatus {
    /** 활성 */
    ACTIVE,

    /** 비활성 */
    INACTIVE,

    /** 정지 */
    SUSPENDED,

    /** 삭제 */
    DELETED,

    /** 승인 대기 (관리자용) */
    PENDING
}