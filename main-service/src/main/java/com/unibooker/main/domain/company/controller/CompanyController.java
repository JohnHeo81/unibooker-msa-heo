package com.unibooker.main.controller;

import com.unibooker.main.dto.CompanyDto;
import com.unibooker.main.entity.CompanyStatus;
import com.unibooker.main.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 기업 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 기업 조회 (ID)
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto.Response> getCompany(@PathVariable Long companyId) {
        log.info("GET /api/companies/{} - 기업 조회", companyId);
        CompanyDto.Response response = companyService.getCompany(companyId);
        return ResponseEntity.ok(response);
    }

    /**
     * 기업 조회 (Slug)
     */
    @GetMapping("/slug/{companySlug}")
    public ResponseEntity<CompanyDto.Response> getCompanyBySlug(@PathVariable String companySlug) {
        log.info("GET /api/companies/slug/{} - 기업 조회", companySlug);
        CompanyDto.Response response = companyService.getCompanyBySlug(companySlug);
        return ResponseEntity.ok(response);
    }

    /**
     * 상태별 기업 목록 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CompanyDto.Response>> getCompaniesByStatus(@PathVariable CompanyStatus status) {
        log.info("GET /api/companies/status/{} - 상태별 기업 목록 조회", status);
        List<CompanyDto.Response> response = companyService.getCompaniesByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * 기업 승인
     */
    @PostMapping("/{companyId}/approve")
    public ResponseEntity<CompanyDto.Response> approveCompany(
            @PathVariable Long companyId,
            @RequestBody CompanyDto.ApprovalRequest request) {
        log.info("POST /api/companies/{}/approve - 기업 승인", companyId);
        CompanyDto.Response response = companyService.approveCompany(companyId, request.getApprovedBy());
        return ResponseEntity.ok(response);
    }

    /**
     * 기업 거절
     */
    @PostMapping("/{companyId}/reject")
    public ResponseEntity<Void> rejectCompany(
            @PathVariable Long companyId,
            @RequestBody CompanyDto.RejectionRequest request) {
        log.info("POST /api/companies/{}/reject - 기업 거절", companyId);
        companyService.rejectCompany(companyId, request.getRejectionReason());
        return ResponseEntity.ok().build();
    }

    /**
     * 기업 정보 수정
     */
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto.Response> updateCompany(
            @PathVariable Long companyId,
            @RequestBody CompanyDto.UpdateRequest request) {
        log.info("PUT /api/companies/{} - 기업 정보 수정", companyId);
        CompanyDto.Response response = companyService.updateCompany(companyId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 기업 정지
     */
    @PostMapping("/{companyId}/suspend")
    public ResponseEntity<CompanyDto.Response> suspendCompany(@PathVariable Long companyId) {
        log.info("POST /api/companies/{}/suspend - 기업 정지", companyId);
        CompanyDto.Response response = companyService.suspendCompany(companyId);
        return ResponseEntity.ok(response);
    }

    /**
     * 기업 재개
     */
    @PostMapping("/{companyId}/activate")
    public ResponseEntity<CompanyDto.Response> activateCompany(@PathVariable Long companyId) {
        log.info("POST /api/companies/{}/activate - 기업 재개", companyId);
        CompanyDto.Response response = companyService.activateCompany(companyId);
        return ResponseEntity.ok(response);
    }
}

