package com.unibooker.main.domain.user.controller;

import com.unibooker.main.domain.user.model.dto.UserDto;
import com.unibooker.main.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 정보 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.Response> getUser(@PathVariable Long userId) {
        log.info("GET /api/users/{} - 사용자 조회", userId);
        UserDto.Response response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto.UpdateRequest request) {
        log.info("PUT /api/users/{} - 사용자 정보 수정", userId);
        UserDto.Response response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestBody UserDto.PasswordChangeRequest request) {
        log.info("PUT /api/users/{}/password - 비밀번호 변경", userId);
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("DELETE /api/users/{} - 사용자 삭제", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}