package com.unibooker.main.domain.company.model;

/**
 * 기업 상태
 */
public enum CompanyStatus {
    /** 승인 대기 */
    PENDING,

    /** 활성 */
    ACTIVE,

    /** 정지 */
    SUSPENDED,

    /** 거절 */
    REJECTED
}