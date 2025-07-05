# 투자인 (Tuzain) - 주식투자 보조 플랫폼 API 서버

투자인은 사용자에게 주식투자를 보조할 다양한 데이터를 제공하고, MSA로 배포된 AI 서비스를 활용하여 크롤링된 뉴스와 정보를 바탕으로 투자 추천을 제공하는 서비스입니다.

## 주요 기능

### 주식 데이터 분석
- **주식 검색**: 기업명/티커로 주식 검색
- **주가 시계열 데이터**: 기간별 주가 변동 추이
- **시가총액별 정렬**: 시가총액 기준 주식 목록 조회
- **AI 기반 분석**: 재무제표 및 뉴스 기반 AI 투자 분석

### 포트폴리오 관리
- **KIS API 연동**: 한국투자증권 API를 통한 실시간 포트폴리오 조회
- **위시리스트**: 관심 주식 등록/관리
- **포트폴리오 분석**: 보유 주식 현황 및 수익률 분석

### 재무 데이터
- **재무제표 분석**: 손익계산서, 재무상태표, 현금흐름표
- **재무비율 분석**: 다양한 재무비율 지표 제공
- **AI 감정 분석**: 재무 데이터 기반 AI 투자 감정 분석

### 뉴스 및 리포트
- **뉴스 크롤링**: 실시간 주식 관련 뉴스 수집
- **감정 분석**: 뉴스 내용 기반 감정 분석 (긍정/부정/중립)
- **카테고리별 필터링**: 섹터별 뉴스 분류
- **사용자 리포트**: 개인 투자 리포트 작성/공유

### 거시경제 지표
- **경제 지표**: GDP, CPI, PPI, 실업률 등 주요 경제 지표
- **연준 기준금리**: FED 기준금리 추이
- **소비자 물가지수**: PCE, CCI 등 소비 관련 지표

### 사용자 관리
- **회원가입/로그인**: JWT 기반 인증
- **이메일 인증**: 회원가입 시 이메일 인증
- **비밀번호 재설정**: 이메일을 통한 비밀번호 재설정
- **프로필 관리**: 사용자 프로필 정보 관리

## 기술 스택

### Backend
- **Java 17**: 메인 프로그래밍 언어
- **Spring Boot 3.4.3**: 웹 애플리케이션 프레임워크
- **Spring Security**: 인증 및 권한 관리
- **Spring Data JPA**: 데이터 접근 계층
- **MySQL**: 주 데이터베이스
- **Redis**: 캐싱 및 세션 관리
- **JWT**: 토큰 기반 인증
- **Gradle**: 빌드 도구

### Infrastructure
- **Docker**: 컨테이너화
- **Docker Compose**: 멀티 컨테이너 오케스트레이션
- **AWS ECR**: 컨테이너 이미지 저장소

### External APIs
- **KIS API**: 한국투자증권 API (포트폴리오 조회)
- **AI 서비스**: MSA로 배포된 AI 분석 서비스

## 📁 프로젝트 구조

```
StockAssistPlatform/
├── src/main/java/com/help/stockassistplatform/
│   ├── domain/                    # 도메인별 모듈
│   │   ├── financial/            # 재무 데이터
│   │   ├── macro/                # 거시경제 지표
│   │   ├── news/                 # 뉴스 관리
│   │   ├── portfolio/            # 포트폴리오 관리
│   │   ├── report/               # 리포트 관리
│   │   ├── stock/                # 주식 데이터
│   │   ├── user/                 # 사용자 관리
│   │   └── wishlist/             # 위시리스트
│   ├── global/                   # 전역 설정
│   │   ├── common/               # 공통 유틸리티
│   │   ├── config/               # 설정 클래스
│   │   └── jwt/                  # JWT 관련
│   └── StockAssistPlatformApplication.java
├── src/main/resources/
│   ├── application.yml           # 기본 설정
│   ├── application-dev.yml       # 개발 환경 설정
│   ├── application-prod.yml      # 운영 환경 설정
│   └── application-test.yml      # 테스트 환경 설정
└── build.gradle                  # Gradle 설정
```

## API 엔드포인트

### 인증 관련
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃
- `POST /api/auth/refresh` - 토큰 갱신
- `GET /api/auth/verify` - 이메일 인증
- `POST /api/auth/password-reset-request` - 비밀번호 재설정 요청
- `POST /api/auth/password-reset` - 비밀번호 재설정

### 사용자 관리
- `GET /api/users/me` - 프로필 조회
- `PUT /api/users/me` - 프로필 수정
- `PUT /api/users/me/password` - 비밀번호 변경
- `DELETE /api/users/me` - 회원 탈퇴

### 주식 데이터
- `GET /api/stocks/search` - 주식 검색
- `GET /api/stocks/analysis` - AI 주식 분석
- `GET /api/stocks/marketcap` - 시가총액별 정렬
- `GET /api/stocks/summary` - 주식 요약 정보
- `GET /api/stocks/prices` - 주가 시계열 데이터

### 재무 데이터
- `GET /api/financial` - 재무 데이터 조회
- `GET /api/financial?ticker={ticker}` - 특정 기업 재무 상세

### 포트폴리오
- `GET /api/portfolio` - KIS API 포트폴리오 조회

### 위시리스트
- `GET /api/wishlist` - 위시리스트 조회
- `POST /api/wishlist` - 위시리스트 추가
- `DELETE /api/wishlist/{symbol}` - 위시리스트 삭제

### 뉴스
- `GET /api/news` - 뉴스 목록 조회 (카테고리/감정별 필터링)

### 리포트
- `GET /api/reports` - 리포트 목록 조회
- `POST /api/reports` - 사용자 리포트 작성
- `GET /api/reports/{id}` - 리포트 상세 조회
- `PUT /api/reports/{id}` - 리포트 수정
- `DELETE /api/reports/{id}` - 리포트 삭제

### 거시경제
- `GET /api/economy/indicators` - 경제 지표 조회

## 시스템 아키텍처

### AI 시스템 구조
```

```

### MSA 구조
- **API Gateway**: Spring Cloud Gateway
- **User Service**: 사용자 인증 및 관리
- **Stock Service**: 주식 데이터 및 분석
- **News Service**: 뉴스 크롤링 및 감정 분석
- **AI Analysis Service**: AI 기반 투자 분석
- **Portfolio Service**: 포트폴리오 관리

## CI/CD 파이프라인

### GitHub Actions 워크플로우
- **Code Push** → **Build & Test** → **Docker Image Build** → **ECR Push** → **ECS Deployment**

### 자동화 프로세스
- **코드 품질 검사**: Checkstyle, SonarQube
- **자동 테스트**: Unit Test, Integration Test
- **보안 스캔**: Docker 이미지 보안 취약점 검사
- **자동 배포**: Blue-Green 배포 전략

## 배포 환경

### AWS 인프라
- **ECS (Elastic Container Service)**: 컨테이너 오케스트레이션
- **ECR (Elastic Container Registry)**: Docker 이미지 저장소
- **RDS (Relational Database Service)**: MySQL 데이터베이스
- **ElastiCache**: Redis 캐시 서버
- **ALB (Application Load Balancer)**: 로드 밸런싱
- **Route 53**: DNS 관리
- **CloudWatch**: 모니터링 및 로깅

### 환경별 배포
- **Development**: 개발 환경 (dev)
- **Production**: 운영 환경 (prod)

## 현재 서비스 상태

**서비스 중단 상태**

현재 투자인 서비스는 개발 완료 후 아카이브 프로젝트로 전환되어 서비스가 중단된 상태입니다!
이 프로젝트는 팀 토이 프로젝트로, 실제 서비스 운영은 하지 않습니다.

### 개발 완료 기능
- ✅ 사용자 인증 시스템
- ✅ 주식 데이터 API
- ✅ 재무 데이터 분석
- ✅ 뉴스 크롤링 및 감정 분석
- ✅ 포트폴리오 관리
- ✅ AI 기반 투자 분석
- ✅ CI/CD 파이프라인 구축
- ✅ AWS 클라우드 인프라 구축

## 개발 가이드

### 코드 스타일
- Naver Checkstyle 규칙 적용
- EditorConfig 설정 포함
- Lombok, record 사용으로 보일러플레이트 코드 최소화

### 데이터베이스
- Primary DB: 사용자 데이터, 애플리케이션 데이터
- Secondary DB: 크롤링된 뉴스 및 AI 분석 데이터
- Redis: 캐싱, 세션, 토큰 저장

### 보안
- Spring Security 기반 인증/인가
- JWT 토큰 기반 세션 관리
- 비밀번호 암호화 (BCrypt)
- CORS 설정

### 캐싱 전략
- Redis를 활용한 재무 데이터 캐싱
- 정적 데이터 메모리 캐싱
- 주기적인 캐시 갱신

## 프로젝트 성과

### 개발 기간
- **2025년 3월 ~ 2025년 6월** (3개월)

### 팀 구성
- **Backend Developer**: 2명
- **Frontend Developer**: 1명
- **Data Engineer**: 1명
- **AI Engineer**: 1명

### 주요 성과
- **MSA 아키텍처 설계 및 구현**
- **AI 기반 투자 분석 시스템 구축**
- **실시간 뉴스 크롤링 및 감정 분석**
- **AWS 클라우드 인프라 구축**
- **CI/CD 파이프라인 자동화**
- **한국투자증권 API 연동**
