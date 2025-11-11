package com.unibooker.main.domain.user.service;

import com.unibooker.main.domain.user.model.dto.UserDto;
import com.unibooker.main.domain.user.model.entity.Users;
import com.unibooker.main.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 조회
     */
    public UserDto.Response getUser(Long userId) {
        log.info("사용자 조회: {}", userId);

        Users user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return convertToResponse(user);
    }

    /**
     * 사용자 정보 수정
     */
    @Transactional
    public UserDto.Response updateUser(Long userId, UserDto.UpdateRequest request) {
        log.info("사용자 정보 수정: {}", userId);

        Users user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        if (request.getName() != null) user.updateName(request.getName());
        if (request.getPhone() != null) user.updatePhone(request.getPhone());
        if (request.getBirthDate() != null) user.updateBirthDate(request.getBirthDate());
        if (request.getGender() != null) user.updateGender(request.getGender());

        return convertToResponse(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, UserDto.PasswordChangeRequest request) {
        log.info("비밀번호 변경: {}", userId);

        Users user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedPassword);
    }

    /**
     * 사용자 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("사용자 삭제: {}", userId);

        Users user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.delete();
    }

    /**
     * Entity -> Response DTO 변환
     */
    private UserDto.Response convertToResponse(Users user) {
        return UserDto.Response.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .role(user.getRole())
                .status(user.getStatus())
                .companyId(user.getCompanyId())
                .isFirstLogin(user.getIsFirstLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}