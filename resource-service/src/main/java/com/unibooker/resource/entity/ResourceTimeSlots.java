package com.unibooker.resource.entity;

import com.unibooker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * 리소스 타임 슬롯 엔티티
 * - 리소스별 예약 가능 시간대 관리
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_time_slots")
public class ResourceTimeSlots extends BaseEntity {

    /** 리소스 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resources resource;

    /** 시작 시간 */
    @Column(nullable = false)
    private LocalTime startTime;

    /** 종료 시간 */
    @Column(nullable = false)
    private LocalTime endTime;

    /** 요일 */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DayOfWeek dayOfWeek;

    /** 운영 여부 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    /**
     * 요일 대문자 변환
     */
    @PrePersist
    @PreUpdate
    private void upperCaseDayOfWeek() {
        if (this.dayOfWeek != null) {
            this.dayOfWeek = DayOfWeek.valueOf(this.dayOfWeek.name().toUpperCase());
        }
    }

    /**
     * 리소스 설정
     */
    public void setResource(Resources resource) {
        this.resource = resource;
    }

    /**
     * 운영 상태 변경
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}