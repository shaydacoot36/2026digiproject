package com.shay.ai_wrapper_java.controller;

import com.shay.ai_wrapper_java.model.Plan;
import com.shay.ai_wrapper_java.model.User;
import com.shay.ai_wrapper_java.model.Task;
import com.shay.ai_wrapper_java.repository.PlanRepository;
import com.shay.ai_wrapper_java.repository.UserRepository;
import com.shay.ai_wrapper_java.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatabaseController {

    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    // ===== PLAN ENDPOINTS =====
    @PostMapping("/plans")
    public ResponseEntity<Map<String, Object>> createPlan(@RequestBody Plan plan) {
        Plan saved = planRepository.save(plan);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Plan created successfully");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> getAllPlans() {
        List<Plan> plans = planRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", plans.size());
        response.put("data", plans);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<Map<String, Object>> getPlanById(@PathVariable Long id) {
        var plan = planRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (plan.isPresent()) {
            response.put("success", true);
            response.put("data", plan.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plan not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    // ===== USER ENDPOINTS =====
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        User saved = userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User created successfully");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", users.size());
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (user.isPresent()) {
            response.put("success", true);
            response.put("data", user.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    // ===== TASK ENDPOINTS =====
    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Task task) {
        Task saved = taskRepository.save(task);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task created successfully");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tasks.size());
        response.put("data", tasks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable Long id) {
        var task = taskRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (task.isPresent()) {
            response.put("success", true);
            response.put("data", task.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Task not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<Map<String, Object>> getTasksByUserId(@PathVariable Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tasks.size());
        response.put("data", tasks);
        return ResponseEntity.ok(response);
    }

    // ===== DATABASE STATUS =====
    @GetMapping("/database-status")
    public ResponseEntity<Map<String, Object>> databaseStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("database_connected", true);
        status.put("tables", new String[]{"plans", "users", "tasks"});
        status.put("total_plans", planRepository.count());
        status.put("total_users", userRepository.count());
        status.put("total_tasks", taskRepository.count());
        status.put("message", "Database is ready!");
        return ResponseEntity.ok(status);
    }
}
