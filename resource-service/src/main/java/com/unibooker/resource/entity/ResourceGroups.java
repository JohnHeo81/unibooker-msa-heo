package com.unibooker.resource.entity;

import com.unibooker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 리소스 그룹(서비스 그룹) 엔티티
 * - 여러 리소스를 묶는 그룹
 * - 서비스 카테고리별 분류
 */
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_groups")
public class ResourceGroups extends BaseEntity {

    /** 그룹명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 그룹 코드 */
    @Column(length = 50)
    private String groupCode;

    /** 그룹 설명 */
    @Column(length = 500)
    private String description;

    /** 썸네일 이미지 */
    private String thumbnail;

    /** 서비스 카테고리 */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ServiceCategory category;

    /** 상시모집 여부 */
    @Column(name = "is_always_available", nullable = false)
    @Builder.Default
    private Boolean isAlwaysAvailable = false;

    /** 활성화 여부 */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** 조회수 */
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    /** 낙관적 락 버전 */
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    /** 기업 ID (외래키를 ID로 관리) */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 생성자 ID */
    @Column(name = "created_by")
    private Long createdBy;

    /** 수정자 ID */
    @Column(name = "updated_by")
    private Long updatedBy;

    /** 리소스 목록 */
    @OneToMany(mappedBy = "resourceGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resources> resources = new ArrayList<>();

    /**
     * 리소스 그룹 정보 수정
     */
    public void update(String name, String groupCode, String description,
                       String thumbnail, ServiceCategory category,
                       Boolean isAlwaysAvailable, Long updatedBy) {
        if (name != null) this.name = name;
        if (groupCode != null) this.groupCode = groupCode;
        if (description != null) this.description = description;
        if (thumbnail != null) this.thumbnail = thumbnail;
        if (category != null) this.category = category;
        if (isAlwaysAvailable != null) this.isAlwaysAvailable = isAlwaysAvailable;
        if (updatedBy != null) this.updatedBy = updatedBy;
    }

    /**
     * 활성화 상태 변경
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 수정자 설정
     */
    public void setUpdatedBy(Long userId) {
        this.updatedBy = userId;
    }
}