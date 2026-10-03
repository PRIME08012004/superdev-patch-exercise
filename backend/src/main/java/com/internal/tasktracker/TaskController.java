package com.internal.tasktracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final int MAX_PAGE_SIZE = 100;

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input and escape LIKE wildcards ('!' is the escape char in the repository)
        String query = q == null ? "" : q.trim();
        String escaped = query.toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        String searchTerm = "%" + escaped + "%";

        // Parse status filter; empty string means "no filter"
        String normalizedStatus = "";
        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid status: " + status));
            }
        }

        // Sanitize paging input
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);

        System.out.println("[TaskController] q=\"" + query + "\" status="
                + (normalizedStatus.isEmpty() ? "ALL" : normalizedStatus)
                + " page=" + safePage + " pageSize=" + safePageSize);

        Page<Task> result = taskRepository.searchTasks(
                searchTerm, normalizedStatus, PageRequest.of(safePage - 1, safePageSize));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", result.getContent());
        response.put("total", result.getTotalElements());
        response.put("page", safePage);
        response.put("pageSize", safePageSize);

        return ResponseEntity.ok(response);
    }
}
