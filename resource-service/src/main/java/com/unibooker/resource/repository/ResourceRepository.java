package com.unibooker.resource.repository;

import com.unibooker.resource.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리소스 레포지토리
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resources, Long> {

    /**
     * 리소스 그룹별 리소스 조회
     */
    List<Resources> findByResourceGroupIdAndDeletedAtIsNull(Long resourceGroupId);

    /**
     * 리소스 그룹별 활성화된 리소스 조회
     */
    List<Resources> findByResourceGroupIdAndIsActiveTrueAndDeletedAtIsNull(Long resourceGroupId);

    /**
     * ID로 조회 (삭제되지 않은 것만)
     */
    Optional<Resources> findByIdAndDeletedAtIsNull(Long id);
}