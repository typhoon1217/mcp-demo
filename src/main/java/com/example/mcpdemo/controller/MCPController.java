package com.example.mcpdemo.controller;

import com.example.mcpdemo.model.MCPMessage;
import com.example.mcpdemo.service.MCPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MCP 컨트롤러 클래스
 * MCP 관련 API 엔드포인트를 처리합니다.
 */
@RestController
@RequestMapping("/api/mcp")
public class MCPController {

  // MCP 서비스 인스턴스
  private final MCPService mcpService;

  /**
   * 생성자를 통한 의존성 주입
   * 
   * @param mcpService MCP 서비스 인스턴스
   */
  @Autowired
  public MCPController(MCPService mcpService) {
    this.mcpService = mcpService;
  }

  /**
   * 쿼리 처리 엔드포인트
   * 클라이언트로부터 받은 쿼리를 처리하고 응답을 반환합니다.
   * 
   * @param query 처리할 쿼리 문자열
   * @return 처리된 MCP 메시지와 HTTP 상태 코드
   */
  @PostMapping("/query")
  public ResponseEntity<MCPMessage> processQuery(@RequestBody String query) {
    // 쿼리를 처리하여 응답 메시지 생성
    MCPMessage response = mcpService.processQuery(query);
    // HTTP 200 OK 상태와 함께 응답 반환
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
