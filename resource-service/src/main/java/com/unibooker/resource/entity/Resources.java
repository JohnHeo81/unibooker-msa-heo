package com.unibooker.resource.entity;

import com.unibooker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 리소스(시설/공간/장비 등) 엔티티
 * - 예약 가능한 리소스의 기본 정보 관리
 */
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resources")
public class Resources extends BaseEntity {

    /** 리소스명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 리소스 설명 */
    @Column(length = 500)
    private String description;

    /** 리소스 이미지 */
    private String resourceImage;

    /** 활성화 여부 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** 예약 시작일 */
    private LocalDate startDate;

    /** 예약 종료일 */
    private LocalDate endDate;

    /** 시간 간격 (분 단위) */
    private Integer timeInterval;

    /** 수용 인원 */
    @Column(nullable = false)
    private Integer capacity;

    /** 좌석 행 개수 */
    private Integer row;

    /** 좌석 열 개수 */
    private Integer col;

    /** 리소스 상태 */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ResourceStatus status;

    /** 낙관적 락 버전 */
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    /** 리소스 그룹 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_group_id")
    private ResourceGroups resourceGroup;

    /** 생성자 ID */
    @Column(name = "created_by")
    private Long createdBy;

    /** 수정자 ID */
    @Column(name = "updated_by")
    private Long updatedBy;

    /** 타임 슬롯 */
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResourceTimeSlots> timeSlots = new ArrayList<>();

    /**
     * 리소스 정보 수정
     */
    public void update(String name, String description, String resourceImage,
                       LocalDate startDate, LocalDate endDate, Integer capacity,
                       Integer row, Integer col, Long updatedBy) {

        // 종료일 검증
        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("종료일이 오늘 이전인 리소스는 수정할 수 없습니다.");
        }

        // 시작일/종료일 검증
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        // 상태 결정
        if (startDate != null && !startDate.isAfter(LocalDate.now())) {
            this.status = ResourceStatus.IN_PROGRESS;
        } else {
            this.status = ResourceStatus.PROGRESS_BEFORE;
        }

        // 필드 업데이트
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (resourceImage != null) this.resourceImage = resourceImage;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
        if (capacity != null) this.capacity = capacity;
        if (row != null) this.row = row;
        if (col != null) this.col = col;
        if (updatedBy != null) this.updatedBy = updatedBy;
    }

    /**
     * 활성화 상태 변경
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 상태 변경
     */
    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    /**
     * 수정자 설정
     */
    public void setUpdatedBy(Long userId) {
        this.updatedBy = userId;
    }

    /**
     * 타임 슬롯 추가
     */
    public void addTimeSlot(ResourceTimeSlots slot) {
        slot.setResource(this);
        this.timeSlots.add(slot);
    }
}