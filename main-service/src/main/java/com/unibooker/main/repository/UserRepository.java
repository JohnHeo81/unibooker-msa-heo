package com.unibooker.main.repository;

import com.unibooker.main.entity.UserRole;
import com.unibooker.main.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 레포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<Users> findByEmailAndDeletedAtIsNull(String email);

    /**
     * 이메일과 기업 ID로 사용자 조회
     */
    Optional<Users> findByEmailAndCompanyIdAndDeletedAtIsNull(String email, Long companyId);

    /**
     * 이메일 중복 확인
     */
    boolean existsByEmailAndCompanyIdAndDeletedAtIsNull(String email, Long companyId);

    /**
     * 기업별 사용자 목록 조회
     */
    List<Users> findByCompanyIdAndDeletedAtIsNull(Long companyId);

    /**
     * 기업별 특정 권한 사용자 목록 조회
     */
    List<Users> findByCompanyIdAndRoleAndDeletedAtIsNull(Long companyId, UserRole role);

    /**
     * ID로 조회 (삭제되지 않은 것만)
     */
    Optional<Users> findByIdAndDeletedAtIsNull(Long id);
}