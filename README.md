# MCP 데모 프로젝트

## 개요
MCP(Multi-Context Processing) 데모는 Gemini API를 활용하여 컨텍스트 기반 쿼리 처리를 구현한 Spring Boot 애플리케이션입니다. 이 프로젝트는 데이터베이스에 저장된 프로젝트 정보를 컨텍스트로 활용하여 사용자 쿼리에 대한 응답을 생성합니다.

## 기술 스택
- Java
- Spring Boot
- Spring Data JPA
- Gemini API
- Lombok
- RESTful API
- Svelte (프론트엔드)
- Vite (빌드 도구)

## 주요 기능
- 프로젝트 정보 CRUD 기능
- 컨텍스트 기반 쿼리 처리
- Gemini API를 활용한 응답 생성
- 직관적인 Svelte 기반 사용자 인터페이스

## 프로젝트 구조
```
mcp-demo/
├── src/main/java/com/example/mcpdemo/
│   ├── config/
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── ApiController.java
│   │   └── MCPController.java
│   ├── model/
│   │   ├── MCPMessage.java
│   │   └── Project.java
│   ├── repository/
│   │   └── ProjectRepository.java
│   ├── service/
│   │   ├── MCPService.java
│   │   └── ProjectService.java
│   └── McpDemoApplication.java
└── svelt-frontend/
    ├── src/
    │   ├── components/
    │   │   ├── MCPDemo.svelte
    │   │   ├── ProjectForm.svelte
    │   │   └── ProjectList.svelte
    │   ├── services/
    │   │   ├── api.js
    │   │   └── mcp.js
    │   ├── App.svelte
    │   └── main.js
    ├── index.html
    └── package.json
```

## 백엔드 API 엔드포인트
- `POST /api/mcp/query`: 컨텍스트 기반 쿼리 처리
- `GET /api/projects`: 모든 프로젝트 조회
- `GET /api/projects/{id}`: 특정 프로젝트 조회
- `POST /api/projects`: 새 프로젝트 생성
- `PUT /api/projects/{id}`: 프로젝트 업데이트
- `DELETE /api/projects/{id}`: 프로젝트 삭제

## 설정
애플리케이션을 실행하기 위해 다음 환경 변수를 설정해야 합니다:
- `gemini.api.key`: Gemini API 키
- `gemini.api.url`: Gemini API 엔드포인트 URL

## 실행 방법

### 백엔드 실행
```bash
./gradlew bootRun
```

### 프론트엔드 실행
```bash
cd svelt-frontend
npm install
npm run dev
```

## 사용 예시
1. 프론트엔드 애플리케이션 접속 (기본: http://localhost:5173)
2. 프로젝트 CRUD 탭에서 프로젝트 생성, 조회, 수정, 삭제
3. MCP 데모 탭에서 프로젝트 관련 질문 입력 및 응답 확인

## 추후 계획
1. MCP를 활용한 컨텍스트 제공 및 분석 기능 확장
   - 더 다양한 데이터 소스에서 컨텍스트 수집
   - 컨텍스트 관련성 분석 알고리즘 개선
   - 컨텍스트 기반 추천 시스템 구현

2. MCP를 활용한 CRUD 제어 구현
   - 자연어 명령을 통한 데이터베이스 CRUD 작업 수행
   - 사용자 의도 분석 및 적절한 CRUD 작업 매핑
   - 안전한 데이터 조작을 위한 검증 시스템 구현

## 라이센스
이 프로젝트는 MIT 라이센스 하에 배포됩니다.

# MCP 데모 프로젝트 - Svelte 프론트엔드

## 개요
이 프로젝트는 MCP(Multi-Context Processing) 데모 애플리케이션의 프론트엔드 부분으로, Svelte와 Vite를 사용하여 개발되었습니다. Spring Boot 백엔드와 통신하여 프로젝트 관리 및 MCP 기반 쿼리 처리 기능을 제공합니다.

## 기술 스택
- Svelte 5.20.2
- Vite 6.2.0
- JavaScript (ES6+)
- Bootstrap (스타일링)
- RESTful API 통신

## 주요 기능
1. **프로젝트 관리 (CRUD)**
   - 프로젝트 목록 조회
   - 새 프로젝트 생성
   - 프로젝트 정보 수정
   - 프로젝트 삭제

2. **MCP 데모**
   - 자연어 쿼리 입력
   - 컨텍스트 기반 응답 표시
   - 사용된 컨텍스트 정보 확인
   - 모델 정보 표시

## 프로젝트 구조
```
svelt-frontend/
├── src/
│   ├── components/
│   │   ├── MCPDemo.svelte    # MCP 쿼리 인터페이스
│   │   ├── ProjectForm.svelte # 프로젝트 생성/수정 폼
│   │   └── ProjectList.svelte # 프로젝트 목록 표시
│   ├── services/
│   │   ├── api.js            # 프로젝트 CRUD API 호출
│   │   └── mcp.js            # MCP 쿼리 API 호출
│   ├── App.svelte            # 메인 애플리케이션 컴포넌트
│   ├── main.js               # 애플리케이션 진입점
│   └── app.css               # 전역 스타일
├── index.html                # HTML 템플릿
├── package.json              # 의존성 및 스크립트
└── vite.config.js            # Vite 설정
```

## 설치 및 실행
```bash
# 의존성 설치
npm install

# 개발 서버 실행 (기본 포트: 5173)
npm run dev

# 프로덕션용 빌드
npm run build

# 빌드된 앱 미리보기
npm run preview
```

## 컴포넌트 설명

### App.svelte
메인 애플리케이션 컴포넌트로, 탭 기반 인터페이스를 제공합니다:
- 프로젝트 CRUD 탭
- MCP 데모 탭

### ProjectList.svelte
프로젝트 목록을 표시하고 다음 기능을 제공합니다:
- 프로젝트 목록 조회
- 프로젝트 수정 모드 활성화
- 프로젝트 삭제

### ProjectForm.svelte
프로젝트 생성 및 수정 폼을 제공합니다:
- 새 프로젝트 생성
- 기존 프로젝트 정보 수정
- 폼 유효성 검사

### MCPDemo.svelte
MCP 쿼리 인터페이스를 제공합니다:
- 자연어 쿼리 입력
- 응답 표시
- 사용된 컨텍스트 정보 표시
- 모델 정보 표시

## API 서비스

### api.js
프로젝트 관련 CRUD API 호출을 처리합니다:
- `getAllProjects()`: 모든 프로젝트 조회
- `getProjectById(id)`: 특정 프로젝트 조회
- `createProject(project)`: 새 프로젝트 생성
- `updateProject(id, project)`: 프로젝트 업데이트
- `deleteProject(id)`: 프로젝트 삭제

### mcp.js
MCP 관련 API 호출을 처리합니다:
- `sendQueryToMCP(query)`: MCP 쿼리 전송 및 응답 수신
