package com.example.mcpdemo.service;

import com.example.mcpdemo.model.MCPMessage;
import com.example.mcpdemo.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MCPService {

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  private final ProjectService projectService;
  private final RestTemplate restTemplate = new RestTemplate();

  public MCPService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public MCPMessage processQuery(String query) {
    // 데이터베이스에서 컨텍스트 가져오기
    List<Project> projects = projectService.getAllProjects();
    List<String> contextStrings = new ArrayList<>();

    // 프로젝트에서 컨텍스트 생성
    for (Project project : projects) {
      contextStrings.add("Project ID: " + project.getId() +
          ", Name: " + project.getName() +
          ", Description: " + project.getDescription() +
          ", Status: " + project.getStatus());
    }

    // 쿼리와 컨텍스트로 Gemini API 호출
    String response = callGeminiApi(query, contextStrings);

    // MCPMessage 생성 및 반환
    MCPMessage mcpMessage = new MCPMessage();
    mcpMessage.setQuery(query);
    mcpMessage.setResponse(response);
    mcpMessage.setContext(contextStrings);
    mcpMessage.setModelUsed("Gemini Pro via MCP");

    return mcpMessage;
  }

  private String callGeminiApi(String query, List<String> context) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-goog-api-key", apiKey);

    Map<String, Object> requestBody = new HashMap<>();

    // contents 배열 구성
    List<Map<String, Object>> contents = new ArrayList<>();
    Map<String, Object> content = new HashMap<>();

    List<Map<String, Object>> parts = new ArrayList<>();

    // 쿼리 추가
    Map<String, Object> queryPart = new HashMap<>();
    queryPart.put("text", "Query: " + query + "\n\nContext: " + String.join("\n", context) +
        "\n\nRespond to the query using the provided context information when relevant.");
    parts.add(queryPart);

    content.put("role", "user");
    content.put("parts", parts);
    contents.add(content);

    requestBody.put("contents", contents);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    try {
      Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);

      if (response != null && response.containsKey("candidates")) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (!candidates.isEmpty()) {
          Map<String, Object> candidate = candidates.get(0);
          @SuppressWarnings("unchecked")
          Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");
          if (!candidateParts.isEmpty()) {
            return (String) candidateParts.get(0).get("text");
          }
        }
      }
      return "응답을 처리할 수 없습니다.";
    } catch (Exception e) {
      e.printStackTrace();
      return "Error calling Gemini API: " + e.getMessage();
    }
  }
}
