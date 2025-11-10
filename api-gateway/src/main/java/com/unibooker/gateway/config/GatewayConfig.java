package com.unibooker.gateway.config;

import com.unibooker.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API Gateway 라우팅 설정
 * - JWT 필터를 모든 라우트에 적용
 * - 서비스별 경로 라우팅 설정
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Main Service 라우팅
                .route("main-service", r -> r
                        .path("/api/auth/**", "/api/users/**", "/api/companies/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8081"))

                // Resource Service 라우팅
                .route("resource-service", r -> r
                        .path("/api/resources/**", "/api/resource-groups/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8082"))

                // Reservation Service 라우팅
                .route("reservation-service", r -> r
                        .path("/api/reservations/**", "/api/analytics/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8083"))

                // Queue Service 라우팅
                .route("queue-service", r -> r
                        .path("/api/queue/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8084"))

                .build();
    }
}