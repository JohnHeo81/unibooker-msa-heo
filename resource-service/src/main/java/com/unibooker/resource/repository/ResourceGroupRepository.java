package com.unibooker.resource.repository;

import com.unibooker.resource.entity.ResourceGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리소스 그룹 레포지토리
 */
@Repository
public interface ResourceGroupRepository extends JpaRepository<ResourceGroups, Long> {

    /**
     * 회사별 리소스 그룹 조회
     */
    List<ResourceGroups> findByCompanyIdAndDeletedAtIsNull(Long companyId);

    /**
     * 회사별 활성화된 리소스 그룹 조회
     */
    List<ResourceGroups> findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(Long companyId);

    /**
     * 그룹 코드로 조회
     */
    Optional<ResourceGroups> findByGroupCodeAndDeletedAtIsNull(String groupCode);

    /**
     * ID로 조회 (삭제되지 않은 것만)
     */
    Optional<ResourceGroups> findByIdAndDeletedAtIsNull(Long id);
}