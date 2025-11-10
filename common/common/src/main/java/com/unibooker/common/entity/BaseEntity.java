package com.unibooker.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 공통 엔티티 베이스 클래스
 * - ID, 생성일시, 수정일시, 삭제일시 관리
 * - Soft Delete 지원
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    /** 기본키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 생성일시 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 수정일시 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 삭제일시 (Soft Delete) */
    private LocalDateTime deletedAt;

    /**
     * 엔티티 생성 시 자동 호출
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 시 자동 호출
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft Delete 처리
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}