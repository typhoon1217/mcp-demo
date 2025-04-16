package com.example.mcpdemo.controller;

import com.example.mcpdemo.model.MCPMessage;
import com.example.mcpdemo.service.MCPActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mcp")
public class MCPActionController {

  private final MCPActionService mcpActionService;

  @Autowired
  public MCPActionController(MCPActionService mcpActionService) {
    this.mcpActionService = mcpActionService;
  }

  @PostMapping("/action")
  public ResponseEntity<MCPMessage> processActionRequest(@RequestBody String query) {
    MCPMessage response = mcpActionService.processActionRequest(query);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
