# Unibooker MSA 전환 프로젝트

## 📌 프로젝트 개요
- **프로젝트명**: Unibooker (대규모 예약 시스템 B2B SaaS)
- **현재 상태**: 모놀리식 (Layered Architecture)
- **목표**: MSA 전환 (API Gateway 중심 아키텍처)
- **전략**: 점진적 분리 + 단계별 테스트 (원본 절대 보존)
- **보안**: API Gateway에서 JWT 검증, 서비스 우회 불가 구조
- **배포**: Docker Compose (개발/테스트) → Kubernetes (운영)
- **빌드 도구**: Gradle 7.x+

---

## 🏗️ 목표 아키텍처

### 전체 구조
```
외부 사용자
    ↓
API Gateway (8080) ← 단일 진입점
    ├─ JWT 검증 ✅
    ├─ 라우팅
    ├─ Rate Limiting
    └─ CORS
    ↓
[내부 네트워크 - 외부 접근 차단]
    ↓
┌──────────┬──────────┬──────────┬──────────┐
│Main      │Resource  │Reserv.   │Queue     │
│(8081)    │(8082)    │(8083)    │(8084)    │
│          │          │          │          │
│User      │Resource  │Reserv.   │대기열    │
│Company   │Custom    │Analytics │Kafka     │
│Notif.    │Field     │          │Producer  │
└──────────┴──────────┴──────────┴──────────┘
    ↓
[인프라]
MariaDB, Redis, Kafka, Zookeeper
```

### 보안 구조
- ✅ **외부 → Gateway만 접근 가능**
- ✅ **Gateway → 서비스 (JWT 검증 후)**
- ✅ **서비스 간 내부 통신 (JWT 불필요)**
- ✅ **서비스 직접 접근 불가 (네트워크 분리)**

---

## 🎯 MSA 서비스 구성 (7개)

### 1. common (공통 라이브러리)
```
역할: 모든 서비스가 사용하는 공통 코드
타입: jar 라이브러리
포트: 없음
빌드: Gradle

포함:
✅ BaseResponse, BaseException, BaseResponseStatus
✅ 공통 DTO (UserDto, CompanyDto) - 최소 정보만
✅ Enum (UserRole, UserStatus, CompanyStatus)
✅ JwtUtil (토큰 생성/검증 유틸)
✅ DateUtil, ValidationUtil

제외:
❌ Entity, Repository, Service (각 서비스별)
❌ AuthDto (main-service 전용)
❌ BaseEntity (각 서비스별 구현)

배포:
gradle clean build
gradle publishToMavenLocal
```

### 2. api-gateway (API Gateway)
```
역할: 단일 진입점, JWT 검증, 라우팅
기술: Spring Cloud Gateway
포트: 8080 (외부 노출)
빌드: Gradle

주요 기능:
✅ JWT 검증 필터 (필수!)
✅ 라우팅 (/api/auth → main, /api/resources → resource)
✅ Rate Limiting
✅ CORS 설정
✅ 검증 후 사용자 정보 헤더 추가 (X-User-Id)

특징:
- 로그인/회원가입 API는 JWT 검증 제외
- 나머지 모든 API는 JWT 검증 필수
- 검증 실패 시 401 Unauthorized
- 서비스들은 이 Gateway를 통해서만 접근 가능
```

### 3. main-service (핵심 도메인)
```
역할: 사용자, 기업, 알림 관리
포트: 8081 (내부 네트워크만, 외부 노출 안 함!)
빌드: Gradle

포함 도메인:
- user (회원가입, 로그인, 회원 관리)
- company (기업 등록, 승인, slug 관리)
- notification (알림 발송, 템플릿 관리)

DB: unibooker_main
테이블: users, companies, notifications, notification_templates

주요 API:
- POST /api/auth/login (JWT 발행)
- POST /api/auth/signup
- POST /api/auth/refresh
- GET /api/users/{id}
- POST /api/companies
- GET /internal/users/{id} (내부 전용 - JWT 검증 안 함)

외부 통신:
- Redis (세션, Refresh Token)
- Email (SMTP)
- Kafka Producer (알림 발송)

특징:
- JWT 검증 로직 없음 (Gateway가 함)
- @RequestHeader("X-User-Id") 로 사용자 정보 받음
- 내부 API는 /internal 경로로 분리
```

### 4. resource-service (리소스 관리)
```
역할: 예약 가능한 리소스/시설 관리
포트: 8082 (내부 네트워크만)
빌드: Gradle

포함 도메인:
- resource (리소스 CRUD, 조회)
- customfield (기업별 커스텀 필드)

DB: unibooker_resource
테이블: resources, resource_groups, time_slots, custom_fields

주요 API:
- GET /api/resources (리소스 목록)
- POST /api/resources (리소스 생성)
- GET /api/resources/{id}
- GET /internal/resources/{id} (내부 전용)

외부 통신:
- main-service (사용자/기업 정보 조회)
- Redis (리소스 캐싱)

특징:
- JWT 검증 로직 없음
- main-service 호출 시 내부 API 사용
```

### 5. reservation-service (예약 + 분석)
```
역할: 예약 처리 및 통계 분석
포트: 8083 (내부 네트워크만)
빌드: Gradle

포함 도메인:
- reservation (예약 생성, 취소, 조회)
- analytics (예약 통계, 인기 리소스 분석)

DB: unibooker_reservation
테이블: reservations, reservation_logs, analytics_daily, analytics_monthly

주요 API:
- POST /api/reservations (예약 생성)
- DELETE /api/reservations/{id} (예약 취소)
- GET /api/reservations/my (내 예약 목록)
- GET /api/analytics/popular (인기 리소스)

외부 통신:
- main-service (사용자 정보)
- resource-service (리소스 정보)
- queue-service (대기열)
- Kafka Consumer (예약 요청 처리)
- Redis (동시성 제어, Distributed Lock)

특징:
- Kafka 기반 비동기 처리
- Redis Lock으로 중복 예약 방지
- Analytics는 같은 DB 사용 (초기)
```

### 6. queue-service (대기열 관리)
```
역할: 대규모 예약 요청 큐잉 및 순차 처리
포트: 8084 (내부 네트워크만)
빌드: Gradle

DB: 없음 (Stateless)

주요 API:
- POST /api/queue/enqueue (예약 요청 큐 추가)
- GET /api/queue/status/{requestId} (처리 상태 조회)
- GET /api/queue/position/{userId} (내 대기 순번)

외부 통신:
- Kafka Producer (예약 요청 전송)
- Redis (대기 순번, 실시간 상태)
- reservation-service (처리 결과 수신)

특징:
- 대기열 순번 실시간 제공
- Kafka Topic: reservation-requests
```

### 7. 인프라 (Docker Compose)
```
- MariaDB (서비스별 독립 DB: main, resource, reservation)
- Redis (캐시, 세션, Lock, 대기열 순번)
- Kafka (메시지 큐)
- Zookeeper (Kafka 메타데이터 - 필수)
```

---

## 🗂️ 폴더 구조
```
~/Desktop/unibooker/
├─ be17-fin-LinkVerse-UniBooker-BE/        ← 원본 (절대 수정 금지!)
├─ be17-fin-LinkVerse-UniBooker-FE/
└─ msa-service-LinkVerse-Unibooker-BE/     ← 작업 폴더
   ├─ common/
   │  ├─ build.gradle                      ← Gradle 빌드 설정
   │  ├─ settings.gradle
   │  ├─ gradle/
   │  └─ src/main/java/com/unibooker/common/
   │     ├─ response/
   │     │  ├─ BaseResponse.java
   │     │  └─ ErrorResponse.java
   │     ├─ exception/
   │     │  ├─ BaseException.java
   │     │  └─ BaseResponseStatus.java
   │     ├─ dto/
   │     │  ├─ UserDto.java
   │     │  └─ CompanyDto.java
   │     ├─ enums/
   │     │  ├─ UserRole.java
   │     │  ├─ UserStatus.java
   │     │  └─ CompanyStatus.java
   │     └─ util/
   │        ├─ JwtUtil.java
   │        ├─ DateUtil.java
   │        └─ ValidationUtil.java
   │
   ├─ api-gateway/
   ├─ main-service/
   ├─ resource-service/
   ├─ reservation-service/
   ├─ queue-service/
   ├─ settings.gradle                      ← 멀티 프로젝트 설정
   ├─ build.gradle                         ← 루트 빌드 설정
   ├─ docker-compose.yml
   └─ README.md
```

---

## 📋 단계별 작업 계획

### Phase 1: 환경 준비 ✅
- [x] 작업 폴더 생성 확인
- [x] 원본 프로젝트 백업 확인
- [ ] IntelliJ 멀티 프로젝트 열기

---

### Phase 2: Common 모듈 구현 (진행 중 - 80%)

#### 2.1 프로젝트 생성 ✅
- [x] common 프로젝트 생성 완료

#### 2.2 build.gradle 작성 ✅
```gradle
plugins {
    id 'java-library'
}

group = 'com.unibooker'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'
    api 'com.fasterxml.jackson.core:jackson-annotations:2.15.3'
    api 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}

apply plugin: 'maven-publish'

publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
            groupId = 'com.unibooker'
            artifactId = 'common'
            version = '1.0.0'
        }
    }
}
```

#### 2.3 BaseResponse 작성 ✅
- [x] 성공/에러 응답 완성

#### 2.4 BaseException 작성 ✅
- [x] RuntimeException 상속 완성

#### 2.5 BaseResponseStatus 작성 ❌ (필수!)
```java
package com.unibooker.common.exception;

/**
 * API 응답 상태 코드 정의
 */
public enum BaseResponseStatus {
    // ========== 성공 (1xxxx) ==========
    SUCCESS(10000, "요청에 성공하였습니다."),
    
    // ========== 요청 오류 (2xxxx) ==========
    BAD_REQUEST(20000, "잘못된 요청입니다."),
    INVALID_INPUT(20001, "입력값이 올바르지 않습니다."),
    
    // 사용자 관련
    USER_NOT_FOUND(20010, "존재하지 않는 사용자입니다."),
    DUPLICATE_EMAIL(20011, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(20012, "비밀번호가 일치하지 않습니다."),
    
    // 기업 관련
    COMPANY_NOT_FOUND(20020, "존재하지 않는 기업입니다."),
    DUPLICATE_SLUG(20021, "이미 사용 중인 Slug입니다."),
    
    // ========== 인증/인가 오류 (3xxxx) ==========
    UNAUTHORIZED(30000, "인증이 필요합니다."),
    JWT_INVALID(30001, "유효하지 않은 토큰입니다."),
    JWT_EXPIRED(30002, "만료된 토큰입니다."),
    FORBIDDEN(30003, "권한이 없습니다."),
    
    // ========== 비즈니스 로직 오류 (4xxxx) ==========
    RESERVATION_UNAVAILABLE(40001, "예약 불가능한 시간입니다."),
    CAPACITY_EXCEEDED(40002, "정원이 초과되었습니다."),
    DUPLICATE_RESERVATION(40003, "이미 예약이 존재합니다."),
    LOCK_ACQUISITION_FAILED(40004, "동시 예약 처리 중입니다."),
    
    // ========== 서버 오류 (5xxxx) ==========
    INTERNAL_SERVER_ERROR(50000, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(50001, "데이터베이스 연결에 실패했습니다.");
    
    private final int code;
    private final String message;
    
    BaseResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
```

#### 2.6 공통 DTO 작성 ✅
- [x] UserDto 완성
- [x] CompanyDto 완성

#### 2.7 Enum 작성 ✅
- [x] UserRole 완성
- [x] UserStatus 완성
- [x] CompanyStatus 완성

#### 2.8 JwtUtil 작성 ✅
- [x] JWT 생성/검증 완성

#### 2.9 빌드 & 로컬 배포 ⏳ (대기 중)
```bash
cd common/common
gradle clean build
gradle publishToMavenLocal
```

**완료 조건**: BaseResponseStatus 작성 + 로컬 배포 성공

---

### Phase 3: API Gateway 구현 (대기)
### Phase 4: Resource Service 분리 (대기)
### Phase 5: Main Service 분리 (대기)
### Phase 6: 통합 테스트 (대기)
### Phase 7: Reservation Service 분리 (대기)
### Phase 8: Queue Service 구현 (대기)
### Phase 9: Docker Compose 구성 (대기)
### Phase 10: 통합 테스트 & 문서화 (대기)

---

## 📚 참고 명령어

### Gradle

```bash
# Common 모듈 빌드 및 로컬 배포 (필수!)
cd common/common
gradle clean build
gradle publishToMavenLocal

# 빌드 확인
ls ~/.m2/repository/com/unibooker/common/1.0.0/

# 서비스 실행
gradle bootRun

# 전체 빌드
gradle clean build

# 의존성 확인
gradle dependencies
```

---

## 🚀 현재 진행 상황

**현재 Phase**: Phase 2 진행 중 (80% 완료)
**다음 작업**: BaseResponseStatus.java 작성
**진행률**: 15% → 100%

**체크리스트**:
- [x] Phase 1: 환경 준비
- [ ] Phase 2: Common 모듈 (BaseResponseStatus 작성 필요)
- [ ] Phase 3: API Gateway
- [ ] Phase 4: Resource Service
- [ ] Phase 5: Main Service
- [ ] Phase 6: 통합 테스트 (3개)
- [ ] Phase 7: Reservation Service
- [ ] Phase 8: Queue Service
- [ ] Phase 9: Docker Compose
- [ ] Phase 10: 최종 테스트

---

**프로젝트 시작일**: 2025-01-10  
**최종 업데이트**: Phase 2 진행 중 (80% 완료)  
**목표 완료일**: 2025-02-28  
**빌드 도구**: Gradle 7.x+
