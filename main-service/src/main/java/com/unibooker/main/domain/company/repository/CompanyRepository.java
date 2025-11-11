package com.unibooker.main.domain.company.repository;

import com.unibooker.main.domain.company.model.entity.Companies;
import com.unibooker.main.domain.company.model.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 기업 레포지토리
 */
@Repository
public interface CompanyRepository extends JpaRepository<Companies, Long> {

    /**
     * Slug로 기업 조회
     */
    Optional<Companies> findByCompanySlugAndDeletedAtIsNull(String companySlug);

    /**
     * 사업자등록번호로 기업 조회
     */
    Optional<Companies> findByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    /**
     * 사업자등록번호 중복 확인
     */
    boolean existsByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    /**
     * Slug 중복 확인
     */
    boolean existsByCompanySlugAndDeletedAtIsNull(String companySlug);

    /**
     * 상태별 기업 목록 조회
     */
    List<Companies> findByStatusAndDeletedAtIsNull(CompanyStatus status);

    /**
     * ID로 조회 (삭제되지 않은 것만)
     */
    Optional<Companies> findByIdAndDeletedAtIsNull(Long id);
}