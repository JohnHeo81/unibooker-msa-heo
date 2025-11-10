package com.unibooker.resource.repository;

import com.unibooker.resource.entity.ResourceTimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 리소스 타임 슬롯 레포지토리
 */
@Repository
public interface ResourceTimeSlotRepository extends JpaRepository<ResourceTimeSlots, Long> {

    /**
     * 리소스별 타임 슬롯 조회
     */
    List<ResourceTimeSlots> findByResourceIdAndDeletedAtIsNull(Long resourceId);

    /**
     * 리소스별 활성화된 타임 슬롯 조회
     */
    List<ResourceTimeSlots> findByResourceIdAndIsActiveTrueAndDeletedAtIsNull(Long resourceId);
}
