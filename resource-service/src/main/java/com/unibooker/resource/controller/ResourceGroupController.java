package com.unibooker.resource.controller;

import com.unibooker.resource.dto.ResourceGroupDto;
import com.unibooker.resource.service.ResourceGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리소스 그룹 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/resource-groups")
@RequiredArgsConstructor
public class ResourceGroupController {

    private final ResourceGroupService resourceGroupService;

    /**
     * 리소스 그룹 생성
     */
    @PostMapping
    public ResponseEntity<ResourceGroupDto.Response> createResourceGroup(
            @RequestBody ResourceGroupDto.CreateRequest request) {
        log.info("POST /api/resource-groups - 리소스 그룹 생성: {}", request.getName());
        ResourceGroupDto.Response response = resourceGroupService.createResourceGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리소스 그룹 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceGroupDto.Response> getResourceGroup(@PathVariable Long id) {
        log.info("GET /api/resource-groups/{} - 리소스 그룹 조회", id);
        ResourceGroupDto.Response response = resourceGroupService.getResourceGroup(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 회사별 리소스 그룹 목록 조회
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ResourceGroupDto.Response>> getResourceGroupsByCompany(
            @PathVariable Long companyId) {
        log.info("GET /api/resource-groups/company/{} - 회사별 리소스 그룹 목록 조회", companyId);
        List<ResourceGroupDto.Response> response = resourceGroupService.getResourceGroupsByCompany(companyId);
        return ResponseEntity.ok(response);
    }

    /**
     * 리소스 그룹 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceGroupDto.Response> updateResourceGroup(
            @PathVariable Long id,
            @RequestBody ResourceGroupDto.UpdateRequest request) {
        log.info("PUT /api/resource-groups/{} - 리소스 그룹 수정", id);
        ResourceGroupDto.Response response = resourceGroupService.updateResourceGroup(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 리소스 그룹 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceGroup(@PathVariable Long id) {
        log.info("DELETE /api/resource-groups/{} - 리소스 그룹 삭제", id);
        resourceGroupService.deleteResourceGroup(id);
        return ResponseEntity.noContent().build();
    }
}