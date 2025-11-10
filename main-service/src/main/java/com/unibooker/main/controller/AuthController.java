package com.unibooker.main.controller;

import com.unibooker.main.dto.AuthDto;
import com.unibooker.main.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        log.info("POST /api/auth/login - 로그인: {}", request.getEmail());
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 일반 사용자 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignUpResponse> signUp(@RequestBody AuthDto.SignUpRequest request) {
        log.info("POST /api/auth/signup - 회원가입: {}", request.getEmail());
        AuthDto.SignUpResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 관리자 회원가입
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<AuthDto.SignUpResponse> adminSignUp(@RequestBody AuthDto.AdminSignUpRequest request) {
        log.info("POST /api/auth/admin/signup - 관리자 회원가입: {}", request.getEmail());
        AuthDto.SignUpResponse response = authService.adminSignUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.LoginResponse> refreshToken(@RequestBody AuthDto.RefreshTokenRequest request) {
        log.info("POST /api/auth/refresh - 토큰 갱신");
        AuthDto.LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}