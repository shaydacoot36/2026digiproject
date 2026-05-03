package com.shay.ai_wrapper_java.controller;

import com.shay.ai_wrapper_java.model.Task;
import com.shay.ai_wrapper_java.model.User;
import com.shay.ai_wrapper_java.repository.TaskRepository;
import com.shay.ai_wrapper_java.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Task task) {
        // Verify user exists
        if (task.getUser() == null || task.getUser().getUserId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        var user = userRepository.findById(task.getUser().getUserId());
        if (user.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        task.setUser(user.get());
        Task saved = taskRepository.save(task);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task created successfully");
        response.put("data", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tasks.size());
        response.put("data", tasks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable Long taskId) {
        var task = taskRepository.findById(taskId);
        Map<String, Object> response = new HashMap<>();
        if (task.isPresent()) {
            response.put("success", true);
            response.put("data", task.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getTasksByUserId(@PathVariable Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Task> tasks = taskRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tasks.size());
        response.put("data", tasks);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        var task = taskRepository.findById(taskId);
        Map<String, Object> response = new HashMap<>();
        if (task.isPresent()) {
            Task existingTask = task.get();
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setDueDate(taskDetails.getDueDate());
            Task updated = taskRepository.save(existingTask);
            response.put("success", true);
            response.put("message", "Task updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long taskId) {
        Map<String, Object> response = new HashMap<>();
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            response.put("success", true);
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
