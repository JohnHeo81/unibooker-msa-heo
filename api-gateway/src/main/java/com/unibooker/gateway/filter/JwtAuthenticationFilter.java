package com.unibooker.gateway.filter;

import com.unibooker.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT 인증 필터
 * - 모든 요청에서 JWT 토큰 검증
 * - 검증 성공 시 사용자 정보를 헤더에 추가
 * - 검증 실패 시 401 Unauthorized 반환
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    public JwtAuthenticationFilter() {
        super(Config.class);
        // JwtUtil은 나중에 초기화
        this.jwtUtil = null;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // JWT 검증 제외 경로 (로그인, 회원가입)
            if (isExcludedPath(path)) {
                log.info("JWT 검증 제외 경로: {}", path);
                return chain.filter(exchange);
            }

            // Authorization 헤더에서 토큰 추출
            String token = extractToken(request);

            if (token == null) {
                log.warn("JWT 토큰이 없습니다. 경로: {}", path);
                return onError(exchange, "JWT 토큰이 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            try {
                // JwtUtil 초기화 (최초 1회)
                if (jwtUtil == null) {
                    initJwtUtil();
                }

                // 토큰 검증
                if (!jwtUtil.validateToken(token)) {
                    log.warn("JWT 토큰이 유효하지 않습니다. 경로: {}", path);
                    return onError(exchange, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
                }

                // 토큰에서 사용자 정보 추출
                Long userId = jwtUtil.getUserId(token);
                String email = jwtUtil.getEmail(token);
                String role = jwtUtil.getRole(token);
                Long companyId = jwtUtil.getCompanyId(token);

                // 헤더에 사용자 정보 추가 (서비스에서 사용)
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .header("X-Company-Id", companyId != null ? String.valueOf(companyId) : "")
                        .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                log.info("JWT 검증 성공. userId: {}, email: {}, role: {}", userId, email, role);
                return chain.filter(modifiedExchange);

            } catch (Exception e) {
                log.error("JWT 검증 중 오류 발생: {}", e.getMessage());
                return onError(exchange, "토큰 검증 실패", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * JWT 검증 제외 경로 확인
     */
    private boolean isExcludedPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/");
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     */
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JwtUtil 초기화
     */
    private void initJwtUtil() {
        if (this.jwtUtil == null) {
            synchronized (this) {
                if (this.jwtUtil == null) {
                    JwtUtil newJwtUtil = new JwtUtil(jwtSecret, accessTokenValidity, refreshTokenValidity);
                    // reflection으로 final 필드 수정 (임시 방편)
                    try {
                        java.lang.reflect.Field field = JwtAuthenticationFilter.class.getDeclaredField("jwtUtil");
                        field.setAccessible(true);
                        field.set(this, newJwtUtil);
                    } catch (Exception e) {
                        log.error("JwtUtil 초기화 실패", e);
                    }
                }
            }
        }
    }

    /**
     * 에러 응답 반환
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String errorResponse = String.format("{\"code\":%d,\"message\":\"%s\"}",
                httpStatus.value(), message);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes()))
        );
    }

    public static class Config {
        // Configuration properties (필요시 추가)
    }
}