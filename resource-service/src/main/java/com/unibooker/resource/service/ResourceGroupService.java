package com.unibooker.resource.service;

import com.unibooker.resource.dto.ResourceGroupDto;
import com.unibooker.resource.entity.ResourceGroups;
import com.unibooker.resource.entity.ResourceStatus;
import com.unibooker.resource.repository.ResourceGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리소스 그룹 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceGroupService {

    private final ResourceGroupRepository resourceGroupRepository;

    /**
     * 리소스 그룹 생성
     */
    @Transactional
    public ResourceGroupDto.Response createResourceGroup(ResourceGroupDto.CreateRequest request) {
        log.info("리소스 그룹 생성: {}", request.getName());

        ResourceGroups resourceGroup = ResourceGroups.builder()
                .name(request.getName())
                .groupCode(request.getGroupCode())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .category(request.getCategory())
                .isAlwaysAvailable(request.getIsAlwaysAvailable() != null ? request.getIsAlwaysAvailable() : false)
                .isActive(true)
                .viewCount(0)
                .companyId(request.getCompanyId())
                .createdBy(request.getCreatedBy())
                .build();

        ResourceGroups saved = resourceGroupRepository.save(resourceGroup);
        return convertToResponse(saved);
    }

    /**
     * 리소스 그룹 조회
     */
    public ResourceGroupDto.Response getResourceGroup(Long id) {
        log.info("리소스 그룹 조회: {}", id);

        ResourceGroups resourceGroup = resourceGroupRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스 그룹을 찾을 수 없습니다: " + id));

        return convertToResponse(resourceGroup);
    }

    /**
     * 회사별 리소스 그룹 목록 조회
     */
    public List<ResourceGroupDto.Response> getResourceGroupsByCompany(Long companyId) {
        log.info("회사별 리소스 그룹 목록 조회: {}", companyId);

        List<ResourceGroups> resourceGroups = resourceGroupRepository
                .findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(companyId);

        return resourceGroups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 리소스 그룹 수정
     */
    @Transactional
    public ResourceGroupDto.Response updateResourceGroup(Long id, ResourceGroupDto.UpdateRequest request) {
        log.info("리소스 그룹 수정: {}", id);

        ResourceGroups resourceGroup = resourceGroupRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스 그룹을 찾을 수 없습니다: " + id));

        resourceGroup.update(
                request.getName(),
                request.getGroupCode(),
                request.getDescription(),
                request.getThumbnail(),
                request.getCategory(),
                request.getIsAlwaysAvailable(),
                request.getUpdatedBy()
        );

        return convertToResponse(resourceGroup);
    }

    /**
     * 리소스 그룹 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteResourceGroup(Long id) {
        log.info("리소스 그룹 삭제: {}", id);

        ResourceGroups resourceGroup = resourceGroupRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("리소스 그룹을 찾을 수 없습니다: " + id));

        resourceGroup.softDelete();
    }

    /**
     * Entity -> Response DTO 변환
     */
    private ResourceGroupDto.Response convertToResponse(ResourceGroups resourceGroup) {
        return ResourceGroupDto.Response.builder()
                .id(resourceGroup.getId())
                .name(resourceGroup.getName())
                .groupCode(resourceGroup.getGroupCode())
                .description(resourceGroup.getDescription())
                .thumbnail(resourceGroup.getThumbnail())
                .category(resourceGroup.getCategory())
                .isAlwaysAvailable(resourceGroup.getIsAlwaysAvailable())
                .isActive(resourceGroup.getIsActive())
                .viewCount(resourceGroup.getViewCount())
                .companyId(resourceGroup.getCompanyId())
                .createdAt(resourceGroup.getCreatedAt())
                .updatedAt(resourceGroup.getUpdatedAt())
                .build();
    }
}