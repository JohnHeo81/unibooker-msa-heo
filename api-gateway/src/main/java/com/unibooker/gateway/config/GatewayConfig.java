package com.unibooker.gateway.config;

import com.unibooker.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API Gateway 라우팅 설정
 * - 서비스별 라우팅 규칙 정의
 * - JWT 인증 필터 적용
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ========== Main Service - Admin API ==========

                // Auth API (인증 불필요)
                .route("auth-refresh", r -> r
                        .path("/api/auth/refresh")
                        .uri("lb://main-service"))

                // Admin 회원가입 (인증 불필요)
                .route("admin-signup", r -> r
                        .path("/api/admins/signup")
                        .uri("lb://main-service"))

                // Admin 상태 조회 (인증 불필요)
                .route("admin-status", r -> r
                        .path("/api/admins/status")
                        .uri("lb://main-service"))

                // Admin 이메일 확인 (인증 불필요)
                .route("admin-check-email", r -> r
                        .path("/api/admins/check-email")
                        .uri("lb://main-service"))

                // Admin 로그인 (인증 불필요)
                .route("admin-login", r -> r
                        .path("/api/admins/login")
                        .uri("lb://main-service"))

                // Admin API (인증 필요) - logout, /me 등
                .route("admin-protected", r -> r
                        .path("/api/admins/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://main-service"))

                // ========== Main Service - Super API ==========

                // Super 로그인 (인증 불필요)
                .route("super-login", r -> r
                        .path("/api/super/login")
                        .uri("lb://main-service"))

                // Super API (인증 필요)
                .route("super-protected", r -> r
                        .path("/api/super/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://main-service"))

                // ========== Main Service - User API ==========

                // User 회원가입 (인증 불필요)
                .route("user-signup", r -> r
                        .path("/api/users/signup")
                        .uri("lb://main-service"))

                // User 로그인 (인증 불필요)
                .route("user-login", r -> r
                        .path("/api/users/login")
                        .uri("lb://main-service"))

                // User 공개 API (인증 불필요)
                .route("user-public", r -> r
                        .path("/api/users/check-email", "/api/users/accounts",
                                "/api/users/reset-password", "/api/users/find-email")
                        .uri("lb://main-service"))

                // User API (인증 필요)
                .route("user-protected", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://main-service"))

                // ========== Main Service - Company API ==========

                // Company API (인증 필요)
                .route("company-api", r -> r
                        .path("/api/companies/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://main-service"))

                // ========== Main Service - Notification API ==========

                // Notification API (인증 필요)
                .route("notification-api", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://main-service"))

                // ========== Resource Service ==========

                // Resource API (인증 필요)
                .route("resource-api", r -> r
                        .path("/api/resources/**", "/api/resource-groups/**",
                                "/api/resource-time-slots/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://resource-service"))

                // ========== Actuator (Health Check) ==========

                // Actuator (인증 불필요)
                .route("actuator", r -> r
                        .path("/actuator/**")
                        .uri("lb://main-service"))

                .build();
    }
}