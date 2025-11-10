package com.unibooker.resource.service;

import com.unibooker.resource.dto.ResourceDto;
import com.unibooker.resource.entity.ResourceGroups;
import com.unibooker.resource.entity.ResourceStatus;
import com.unibooker.resource.entity.Resources;
import com.unibooker.resource.repository.ResourceGroupRepository;
import com.unibooker.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리소스 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceGroupRepository resourceGroupRepository;

    /**
     * 리소스 생성
     */
    @Transactional
    public ResourceDto.Response createResource(ResourceDto.CreateRequest request) {
        log.info("리소스 생성: {}", request.getName());

        // 리소스 그룹 확인
        ResourceGroups resourceGroup = resourceGroupRepository
                .findByIdAndDeletedAtIsNull(request.getResourceGroupId())
                .orElseThrow(() -> new IllegalArgumentException("리소스 그룹을 찾을 수 없습니다: " + request.getResourceGroupId()));

        // 상태 결정
        ResourceStatus status;
        if (resourceGroup.getIsAlwaysAvailable() ||
                (request.getStartDate() != null && !request.getStartDate().isAfter(LocalDate.now()))) {
            status = ResourceStatus.IN_PROGRESS;
        } else {
            status = ResourceStatus.PROGRESS_BEFORE;
        }

        Resources resource = Resources.builder()
                .name(request.getName())
                .description(request.getDescription())
                .resourceImage(request.getResourceImage())
                .isActive(true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .timeInterval(request.getTimeInterval())
                .capacity(request.getCapacity())
                .row(request.getRow())
                .col(request.getCol())
                .status(status)
                .resourceGroup(resourceGroup)
                .createdBy(request.getCreatedBy())
                .build();

        Resources saved = resourceRepository.save(resource);
        return convertToResponse(saved);
    }

    /**
     * 리소스 조회
     */
    public ResourceDto.Response getResource(Long id) {
        log.info("리소스 조회: {}", id);

        Resources resource = resourceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스를 찾을 수 없습니다: " + id));

        return convertToResponse(resource);
    }

    /**
     * 리소스 그룹별 리소스 목록 조회
     */
    public List<ResourceDto.Response> getResourcesByGroup(Long resourceGroupId) {
        log.info("리소스 그룹별 리소스 목록 조회: {}", resourceGroupId);

        List<Resources> resources = resourceRepository
                .findByResourceGroupIdAndIsActiveTrueAndDeletedAtIsNull(resourceGroupId);

        return resources.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 리소스 수정
     */
    @Transactional
    public ResourceDto.Response updateResource(Long id, ResourceDto.UpdateRequest request) {
        log.info("리소스 수정: {}", id);

        Resources resource = resourceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스를 찾을 수 없습니다: " + id));

        resource.update(
                request.getName(),
                request.getDescription(),
                request.getResourceImage(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCapacity(),
                request.getRow(),
                request.getCol(),
                request.getUpdatedBy()
        );

        return convertToResponse(resource);
    }

    /**
     * 리소스 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteResource(Long id) {
        log.info("리소스 삭제: {}", id);

        Resources resource = resourceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스를 찾을 수 없습니다: " + id));

        resource.softDelete();
    }

    /**
     * Entity -> Response DTO 변환
     */
    private ResourceDto.Response convertToResponse(Resources resource) {
        return ResourceDto.Response.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .resourceImage(resource.getResourceImage())
                .isActive(resource.getIsActive())
                .startDate(resource.getStartDate())
                .endDate(resource.getEndDate())
                .timeInterval(resource.getTimeInterval())
                .capacity(resource.getCapacity())
                .row(resource.getRow())
                .col(resource.getCol())
                .status(resource.getStatus())
                .resourceGroupId(resource.getResourceGroup().getId())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
