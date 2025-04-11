# MCP 데모 프로젝트

## 개요
MCP(Multi-Context Protocol) 데모는 Gemini API를 활용하여 컨텍스트 기반 쿼리 처리를 구현한 Spring Boot 애플리케이션입니다. 이 프로젝트는 데이터베이스에 저장된 프로젝트 정보를 컨텍스트로 활용하여 사용자 쿼리에 대한 응답을 생성합니다.

## 기술 스택
- Java
- Spring Boot
- Spring Data JPA
- Gemini API
- Lombok
- RESTful API

## 주요 기능
- 프로젝트 정보 CRUD 기능
- 컨텍스트 기반 쿼리 처리
- Gemini API를 활용한 응답 생성

## 프로젝트 구조
```
src/main/java/com/example/mcpdemo/
├── config/
│   └── WebConfig.java
├── controller/
│   ├── ApiController.java
│   └── MCPController.java
├── model/
│   ├── MCPMessage.java
│   └── Project.java
├── repository/
│   └── ProjectRepository.java
├── service/
│   ├── MCPService.java
│   └── ProjectService.java
└── McpDemoApplication.java
```
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
