package com.example.mcpdemo.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MCPMessage {

  private String query;
  private String response;
  private List<String> context;
  private String modelUsed;
}

