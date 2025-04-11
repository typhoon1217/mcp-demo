package com.example.mcpdemo.controller;

import com.example.mcpdemo.model.Project;
import com.example.mcpdemo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ApiController {

  private final ProjectService projectService;

  @Autowired
  public ApiController(ProjectService projectService) {
    this.projectService = projectService;
  }

  // 모든 프로젝트 목록을 조회합니다.
  @GetMapping
  public ResponseEntity<List<Project>> getAllProjects() {
    List<Project> projects = projectService.getAllProjects();
    return new ResponseEntity<>(projects, HttpStatus.OK);
  }

  // ID로 특정 프로젝트를 조회합니다.
  @GetMapping("/{id}")
  public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
    Optional<Project> project = projectService.getProjectById(id);
    return project.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  // 새로운 프로젝트를 생성합니다.
  @PostMapping
  public ResponseEntity<Project> createProject(@RequestBody Project project) {
    Project savedProject = projectService.saveProject(project);
    return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
  }

  // 기존 프로젝트를 업데이트합니다.
  @PutMapping("/{id}")
  public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
    Optional<Project> existingProject = projectService.getProjectById(id);

    if (existingProject.isPresent()) {
      project.setId(id);
      Project updatedProject = projectService.saveProject(project);
      return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // 프로젝트를 삭제합니다.
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
    Optional<Project> existingProject = projectService.getProjectById(id);

    if (existingProject.isPresent()) {
      projectService.deleteProject(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
