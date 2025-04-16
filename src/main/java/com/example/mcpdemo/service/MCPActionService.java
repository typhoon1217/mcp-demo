package com.example.mcpdemo.service;

import com.example.mcpdemo.model.MCPMessage;
import com.example.mcpdemo.model.Project;
import com.example.mcpdemo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MCPActionService {

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  private final ProjectService projectService;
  private final ProjectRepository projectRepository;
  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  public MCPActionService(ProjectService projectService, ProjectRepository projectRepository) {
    this.projectService = projectService;
    this.projectRepository = projectRepository;
  }

  public MCPMessage processActionRequest(String userQuery) {
    // Get context from the database
    List<Project> projects = projectService.getAllProjects();
    List<String> contextStrings = new ArrayList<>();

    // Create context from projects
    for (Project project : projects) {
      contextStrings.add("Project ID: " + project.getId() +
          ", Name: " + project.getName() +
          ", Description: " + project.getDescription() +
          ", Status: " + project.getStatus());
    }

    // Gemini API를 호출하여 사용자 쿼리를 분석하고 작업 결정
    String actionPlan = generateActionPlan(userQuery, contextStrings);

    // 작업 계획 실행
    String actionResult = executeAction(actionPlan, userQuery);

    // MCPMessage 생성 및 반환
    MCPMessage mcpMessage = new MCPMessage();
    mcpMessage.setQuery(userQuery);
    mcpMessage.setResponse(actionResult);
    mcpMessage.setContext(contextStrings);
    mcpMessage.setModelUsed("Gemini Pro via MCP with Action Execution");

    return mcpMessage;
  }

  private String generateActionPlan(String query, List<String> context) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-goog-api-key", apiKey);

    Map<String, Object> requestBody = new HashMap<>();

    // 콘텐츠 배열 구성
    List<Map<String, Object>> contents = new ArrayList<>();
    Map<String, Object> content = new HashMap<>();

    List<Map<String, Object>> parts = new ArrayList<>();

    // 쿼리 및 작업 분석 지침 추가
    Map<String, Object> queryPart = new HashMap<>();
    queryPart.put("text", "User Query: " + query + "\n\nAvailable Context: " + String.join("\n", context) +
        "\n\nAnalyze the user query and determine if they want to perform a CRUD operation on projects. " +
        "If yes, respond with a structured action plan in the following format:\n" +
        "ACTION: [CREATE/READ/UPDATE/DELETE]\n" +
        "ENTITY: project\n" +
        "ID: [project_id if applicable for UPDATE/DELETE, otherwise 'null']\n" +
        "DATA: [For CREATE/UPDATE, provide a valid JSON object with these exact fields:\n" +
        "{\n" +
        "  \"name\": \"Project Name\",\n" +
        "  \"description\": \"Project Description\",\n" +
        "  \"status\": \"[Pending/Active/Completed]\"\n" +
        "}\n" +
        "For READ/DELETE, use 'null']\n" +
        "CONFIRMATION: [yes/no - whether to confirm with user before executing]\n\n" +
        "Examples:\n\n" +
        "1. For creating a project:\n" +
        "ACTION: CREATE\n" +
        "ENTITY: project\n" +
        "ID: null\n" +
        "DATA: {\"name\": \"New Website\", \"description\": \"Company website redesign\", \"status\": \"Pending\"}\n"
        +
        "CONFIRMATION: yes\n\n" +
        "2. For updating a project:\n" +
        "ACTION: UPDATE\n" +
        "ENTITY: project\n" +
        "ID: 1\n" +
        "DATA: {\"status\": \"Pending\", \"description\": \"Updated description\"}\n" +
        "CONFIRMATION: yes\n\n" +
        "If no CRUD action is intended, respond with:\n" +
        "ACTION: NONE\n" +
        "RESPONSE: [answer to user query based on context]");
    parts.add(queryPart);

    content.put("role", "user");
    content.put("parts", parts);
    contents.add(content);

    requestBody.put("contents", contents);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    try {
      Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);

      if (response != null && response.containsKey("candidates")) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (!candidates.isEmpty()) {
          Map<String, Object> candidate = candidates.get(0);
          Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
          List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");
          if (!candidateParts.isEmpty()) {
            return (String) candidateParts.get(0).get("text");
          }
        }
      }
      return "ACTION: NONE\nRESPONSE: Sorry, I couldn't process your request.";
    } catch (Exception e) {
      e.printStackTrace();
      return "ACTION: NONE\nRESPONSE: Error calling Gemini API: " + e.getMessage();
    }
  }

  private String executeAction(String actionPlan, String originalQuery) {
    // 작업 계획 분석
    Map<String, String> actionDetails = parseActionPlan(actionPlan);

    String action = actionDetails.getOrDefault("ACTION", "NONE");

    // 작업이 없으면 응답 반환
    if ("NONE".equals(action)) {
      return actionDetails.getOrDefault("RESPONSE", "I couldn't determine what action to take.");
    }

    // CRUD 작업 실행
    try {
      switch (action) {
        case "CREATE":
          return handleCreate(actionDetails);
        case "READ":
          return handleRead(actionDetails);
        case "UPDATE":
          return handleUpdate(actionDetails);
        case "DELETE":
          return handleDelete(actionDetails);
        default:
          return "I couldn't determine what action to take based on your query: \"" + originalQuery + "\"";
      }
    } catch (Exception e) {
      return "Error executing action: " + e.getMessage();
    }
  }

  private Map<String, String> parseActionPlan(String actionPlan) {
    Map<String, String> result = new HashMap<>();

    // 작업 추출
    Pattern actionPattern = Pattern.compile("ACTION:\\s*(\\w+)");
    Matcher actionMatcher = actionPattern.matcher(actionPlan);
    if (actionMatcher.find()) {
      result.put("ACTION", actionMatcher.group(1));
    }

    // 엔티티 추출
    Pattern entityPattern = Pattern.compile("ENTITY:\\s*(\\w+)");
    Matcher entityMatcher = entityPattern.matcher(actionPlan);
    if (entityMatcher.find()) {
      result.put("ENTITY", entityMatcher.group(1));
    }

    // ID 추출
    Pattern idPattern = Pattern.compile("ID:\\s*(\\w+|null)");
    Matcher idMatcher = idPattern.matcher(actionPlan);
    if (idMatcher.find()) {
      result.put("ID", idMatcher.group(1));
    }

    // 데이터 추출 - JSON일 수 있으므로 더 복잡함
    Pattern dataPattern = Pattern.compile("DATA:\\s*(\\{.*?\\}|null)", Pattern.DOTALL);
    Matcher dataMatcher = dataPattern.matcher(actionPlan);
    if (dataMatcher.find()) {
      result.put("DATA", dataMatcher.group(1));
    }

    // 확인 추출
    Pattern confirmPattern = Pattern.compile("CONFIRMATION:\\s*(yes|no)");
    Matcher confirmMatcher = confirmPattern.matcher(actionPlan);
    if (confirmMatcher.find()) {
      result.put("CONFIRMATION", confirmMatcher.group(1));
    }

    // 비작업에 대한 응답 추출
    if ("NONE".equals(result.get("ACTION"))) {
      Pattern responsePattern = Pattern.compile("RESPONSE:\\s*(.+)", Pattern.DOTALL);
      Matcher responseMatcher = responsePattern.matcher(actionPlan);
      if (responseMatcher.find()) {
        result.put("RESPONSE", responseMatcher.group(1).trim());
      }
    }

    return result;
  }

  private String handleCreate(Map<String, String> actionDetails) {
    String data = actionDetails.getOrDefault("DATA", "null");
    if ("null".equals(data)) {
      return "Cannot create project: No project data provided.";
    }

    try {
      // 프로젝트 데이터 파싱
      Project project = parseProjectData(data);

      // 프로젝트 저장
      Project savedProject = projectService.saveProject(project);

      return "Project created successfully!\n" +
          "ID: " + savedProject.getId() + "\n" +
          "Name: " + savedProject.getName() + "\n" +
          "Description: " + savedProject.getDescription() + "\n" +
          "Status: " + savedProject.getStatus();
    } catch (Exception e) {
      return "Failed to create project: " + e.getMessage();
    }
  }

  private String handleRead(Map<String, String> actionDetails) {
    String id = actionDetails.getOrDefault("ID", "null");

    try {
      if (!"null".equals(id)) {
        // 특정 프로젝트 읽기
        Optional<Project> project = projectService.getProjectById(Long.parseLong(id));
        if (project.isPresent()) {
          Project p = project.get();
          return "Project Details:\n" +
              "ID: " + p.getId() + "\n" +
              "Name: " + p.getName() + "\n" +
              "Description: " + p.getDescription() + "\n" +
              "Status: " + p.getStatus();
        } else {
          return "Project with ID " + id + " not found.";
        }
      } else {
        // 모든 프로젝트 읽기
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
          return "No projects found.";
        }

        StringBuilder result = new StringBuilder("Found " + projects.size() + " projects:\n\n");
        for (Project p : projects) {
          result.append("ID: ").append(p.getId()).append("\n");
          result.append("Name: ").append(p.getName()).append("\n");
          result.append("Description: ").append(p.getDescription()).append("\n");
          result.append("Status: ").append(p.getStatus()).append("\n\n");
        }
        return result.toString().trim();
      }
    } catch (Exception e) {
      return "Failed to read project(s): " + e.getMessage();
    }
  }

  private String handleUpdate(Map<String, String> actionDetails) {
    String id = actionDetails.getOrDefault("ID", "null");
    String data = actionDetails.getOrDefault("DATA", "null");

    if ("null".equals(id)) {
      return "Cannot update project: Project ID is required.";
    }

    if ("null".equals(data)) {
      return "Cannot update project: No update data provided.";
    }

    try {
      Long projectId = Long.parseLong(id);
      Optional<Project> existingProject = projectService.getProjectById(projectId);

      if (existingProject.isEmpty()) {
        return "Project with ID " + id + " not found.";
      }

      // 업데이트 데이터 파싱
      Project updateData = parseProjectData(data);

      // 기존 프로젝트를 새 데이터로 업데이트
      Project projectToUpdate = existingProject.get();

      if (updateData.getName() != null && !updateData.getName().isEmpty()) {
        projectToUpdate.setName(updateData.getName());
      }

      if (updateData.getDescription() != null) {
        projectToUpdate.setDescription(updateData.getDescription());
      }

      if (updateData.getStatus() != null && !updateData.getStatus().isEmpty()) {
        projectToUpdate.setStatus(updateData.getStatus());
      }

      // 업데이트된 프로젝트 저장
      Project updatedProject = projectService.saveProject(projectToUpdate);

      return "Project updated successfully!\n" +
          "ID: " + updatedProject.getId() + "\n" +
          "Name: " + updatedProject.getName() + "\n" +
          "Description: " + updatedProject.getDescription() + "\n" +
          "Status: " + updatedProject.getStatus();
    } catch (Exception e) {
      return "Failed to update project: " + e.getMessage();
    }
  }

  private String handleDelete(Map<String, String> actionDetails) {
    String id = actionDetails.getOrDefault("ID", "null");

    if ("null".equals(id)) {
      return "Cannot delete project: Project ID is required.";
    }

    try {
      Long projectId = Long.parseLong(id);
      Optional<Project> existingProject = projectService.getProjectById(projectId);

      if (existingProject.isEmpty()) {
        return "Project with ID " + id + " not found.";
      }

      projectService.deleteProject(projectId);
      return "Project with ID " + id + " successfully deleted.";
    } catch (Exception e) {
      return "Failed to delete project: " + e.getMessage();
    }
  }

  private Project parseProjectData(String jsonData) throws Exception {
    // Validate JSON format
    if (!jsonData.startsWith("{") || !jsonData.endsWith("}")) {
      throw new IllegalArgumentException("Invalid JSON format. Expected object starting with { and ending with }");
    }

    Project project = new Project();

    // Define accepted status values
    Set<String> validStatuses = new HashSet<>(Arrays.asList("Pending", "Active", "Completed"));

    if (jsonData.contains("\"name\":")) {
      Pattern namePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]*)\"");
      Matcher nameMatcher = namePattern.matcher(jsonData);
      if (nameMatcher.find()) {
        String name = nameMatcher.group(1).trim();
        if (name.isEmpty()) {
          throw new IllegalArgumentException("Project name cannot be empty");
        }
        project.setName(name);
      }
    }

    if (jsonData.contains("\"description\":")) {
      Pattern descPattern = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]*)\"");
      Matcher descMatcher = descPattern.matcher(jsonData);
      if (descMatcher.find()) {
        String description = descMatcher.group(1).trim();
        project.setDescription(description);
      }
    }
    if (jsonData.contains("\"status\":")) {
      Pattern statusPattern = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]*)\"");
      Matcher statusMatcher = statusPattern.matcher(jsonData);
      if (statusMatcher.find()) {
        String status = statusMatcher.group(1).trim();
        if (!validStatuses.contains(status)) {
          throw new IllegalArgumentException(
              "Invalid status value. Must be one of: " + String.join(", ", validStatuses));
        }
        project.setStatus(status);
      }
    }

    // For CREATE operation, ensure required fields are present
    if (project.getName() == null || project.getStatus() == null) {
      throw new IllegalArgumentException("For new projects, 'name' and 'status' fields are required");
    }

    return project;
  }
}
