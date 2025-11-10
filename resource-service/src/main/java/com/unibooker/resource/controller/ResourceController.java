package com.unibooker.resource.controller;

import com.unibooker.resource.dto.ResourceDto;
import com.unibooker.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리소스 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * 리소스 생성
     */
    @PostMapping
    public ResponseEntity<ResourceDto.Response> createResource(
            @RequestBody ResourceDto.CreateRequest request) {
        log.info("POST /api/resources - 리소스 생성: {}", request.getName());
        ResourceDto.Response response = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리소스 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto.Response> getResource(@PathVariable Long id) {
        log.info("GET /api/resources/{} - 리소스 조회", id);
        ResourceDto.Response response = resourceService.getResource(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 리소스 그룹별 리소스 목록 조회
     */
    @GetMapping("/group/{resourceGroupId}")
    public ResponseEntity<List<ResourceDto.Response>> getResourcesByGroup(
            @PathVariable Long resourceGroupId) {
        log.info("GET /api/resources/group/{} - 리소스 그룹별 리소스 목록 조회", resourceGroupId);
        List<ResourceDto.Response> response = resourceService.getResourcesByGroup(resourceGroupId);
        return ResponseEntity.ok(response);
    }

    /**
     * 리소스 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto.Response> updateResource(
            @PathVariable Long id,
            @RequestBody ResourceDto.UpdateRequest request) {
        log.info("PUT /api/resources/{} - 리소스 수정", id);
        ResourceDto.Response response = resourceService.updateResource(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 리소스 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        log.info("DELETE /api/resources/{} - 리소스 삭제", id);
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}