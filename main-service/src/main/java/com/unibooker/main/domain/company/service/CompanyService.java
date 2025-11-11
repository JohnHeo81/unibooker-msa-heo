package com.unibooker.main.domain.company.service;

import com.unibooker.main.domain.company.model.dto.CompanyDto;
import com.unibooker.main.domain.company.model.entity.Companies;
import com.unibooker.main.domain.company.model.CompanyStatus;
import com.unibooker.main.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 기업 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * 기업 조회
     */
    public CompanyDto.Response getCompany(Long companyId) {
        log.info("기업 조회: {}", companyId);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        return convertToResponse(company);
    }

    /**
     * Slug로 기업 조회
     */
    public CompanyDto.Response getCompanyBySlug(String companySlug) {
        log.info("기업 조회 (Slug): {}", companySlug);

        Companies company = companyRepository.findByCompanySlugAndDeletedAtIsNull(companySlug)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companySlug));

        return convertToResponse(company);
    }

    /**
     * 상태별 기업 목록 조회
     */
    public List<CompanyDto.Response> getCompaniesByStatus(CompanyStatus status) {
        log.info("상태별 기업 목록 조회: {}", status);

        List<Companies> companies = companyRepository.findByStatusAndDeletedAtIsNull(status);

        return companies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 기업 승인
     */
    @Transactional
    public CompanyDto.Response approveCompany(Long companyId, Long approvedBy) {
        log.info("기업 승인: {} by {}", companyId, approvedBy);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        if (!company.isPending()) {
            throw new IllegalArgumentException("승인 대기 상태의 기업만 승인할 수 있습니다.");
        }

        company.approve(approvedBy);

        return convertToResponse(company);
    }

    /**
     * 기업 거절
     */
    @Transactional
    public void rejectCompany(Long companyId, String rejectionReason) {
        log.info("기업 거절: {}", companyId);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        if (!company.isPending()) {
            throw new IllegalArgumentException("승인 대기 상태의 기업만 거절할 수 있습니다.");
        }

        company.reject(rejectionReason);
        company.softDelete(); // 하드 삭제 대신 소프트 삭제
    }

    /**
     * 기업 정보 수정
     */
    @Transactional
    public CompanyDto.Response updateCompany(Long companyId, CompanyDto.UpdateRequest request) {
        log.info("기업 정보 수정: {}", companyId);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        if (request.getCompanyName() != null) company.updateCompanyName(request.getCompanyName());
        if (request.getLogoUrl() != null) company.updateLogoUrl(request.getLogoUrl());

        return convertToResponse(company);
    }

    /**
     * 기업 정지
     */
    @Transactional
    public CompanyDto.Response suspendCompany(Long companyId) {
        log.info("기업 정지: {}", companyId);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        company.suspend();

        return convertToResponse(company);
    }

    /**
     * 기업 재개
     */
    @Transactional
    public CompanyDto.Response activateCompany(Long companyId) {
        log.info("기업 재개: {}", companyId);

        Companies company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        company.activate();

        return convertToResponse(company);
    }

    /**
     * Entity -> Response DTO 변환
     */
    private CompanyDto.Response convertToResponse(Companies company) {
        return CompanyDto.Response.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .businessNumber(company.getBusinessNumber())
                .companySlug(company.getCompanySlug())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus())
                .approvedAt(company.getApprovedAt())
                .approvedBy(company.getApprovedBy())
                .rejectionReason(company.getRejectionReason())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}