package com.shay.ai_wrapper_java.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== USER ENDPOINT TESTS ====================

    @Test
    public void testCreateUserSuccess() throws Exception {
        String userJson = "{"
            + "\"name\": \"John Student\","
            + "\"email\": \"john@ncea.co.nz\","
            + "\"password\": \"securepass123\""
            + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.name").value("John Student"))
            .andExpect(jsonPath("$.email").value("john@ncea.co.nz"));

        System.out.println("✓ User creation test passed");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", instanceOf(java.util.List.class)));

        System.out.println("✓ Get all users test passed");
    }

    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").exists());

        System.out.println("✓ Get user by ID test passed");
    }

    @Test
    public void testCreateUserMissingEmail() throws Exception {
        String invalidUserJson = "{"
            + "\"name\": \"Jane Student\","
            + "\"password\": \"pass123\""
            + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
            .andExpect(status().isBadRequest());

        System.out.println("✓ Invalid user creation correctly rejected");
    }

    @Test
    public void testEmailValidation() throws Exception {
        // Test invalid email format
        String invalidEmailJson = "{"
            + "\"name\": \"Test User\","
            + "\"email\": \"notanemail\","
            + "\"password\": \"pass123\""
            + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmailJson))
            .andExpect(status().isBadRequest());

        System.out.println("✓ Invalid email format correctly rejected");
    }

    // ==================== TASK ENDPOINT TESTS ====================

    @Test
    public void testCreateTaskSuccess() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Study Math Chapter 5\","
            + "\"description\": \"Review algebra concepts\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.taskId").exists())
            .andExpect(jsonPath("$.title").value("Study Math Chapter 5"))
            .andExpect(jsonPath("$.dueDate").value("2026-05-25"));

        System.out.println("✓ Task creation test passed");
    }

    @Test
    public void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", instanceOf(java.util.List.class)));

        System.out.println("✓ Get all tasks test passed");
    }

    @Test
    public void testTaskDueDateFormat() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Test Task\","
            + "\"description\": \"Test\","
            + "\"dueDate\": \"2026-06-01\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dueDate").matches("^\\d{4}-\\d{2}-\\d{2}$"));

        System.out.println("✓ Task due date format validation passed");
    }

    @Test
    public void testTaskMissingTitle() throws Exception {
        String invalidTaskJson = "{"
            + "\"description\": \"Missing title\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTaskJson))
            .andExpect(status().isBadRequest());

        System.out.println("✓ Task with missing title correctly rejected");
    }

    @Test
    public void testTaskInvalidUserId() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Task\","
            + "\"description\": \"Description\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 99999"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isNotFound());

        System.out.println("✓ Task with invalid user ID correctly rejected");
    }

    // ==================== DATABASE STATUS TESTS ====================

    @Test
    public void testDatabaseStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/database-status")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.database").exists());

        System.out.println("✓ Database status endpoint test passed");
    }

    @Test
    public void testDatabaseConnection() throws Exception {
        mockMvc.perform(get("/api/database-status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("connected"));

        System.out.println("✓ Database connection test passed");
    }

    // ==================== RESPONSE FORMAT TESTS ====================

    @Test
    public void testResponseContentType() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        System.out.println("✓ Response content type test passed");
    }

    @Test
    public void testErrorResponseFormat() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        System.out.println("✓ Error response format test passed");
    }

    // ==================== DATA INTEGRITY TESTS ====================

    @Test
    public void testUserDataNotNull() throws Exception {
        String userJson = "{"
            + "\"name\": \"Data Test User\","
            + "\"email\": \"data@test.com\","
            + "\"password\": \"pass123\""
            + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.email").isNotEmpty());

        System.out.println("✓ User data integrity test passed");
    }

    @Test
    public void testTaskDataNotNull() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Non-null Task\","
            + "\"description\": \"Description\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.taskId").exists())
            .andExpect(jsonPath("$.title").isNotEmpty())
            .andExpect(jsonPath("$.dueDate").isNotEmpty());

        System.out.println("✓ Task data integrity test passed");
    }

    @Test
    public void testUserCreatedAtTimestamp() throws Exception {
        String userJson = "{"
            + "\"name\": \"Timestamp User\","
            + "\"email\": \"timestamp@test.com\","
            + "\"password\": \"pass123\""
            + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdAt").exists());

        System.out.println("✓ User created timestamp test passed");
    }

    @Test
    public void testTaskCreatedAtTimestamp() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Timestamp Task\","
            + "\"description\": \"Test\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdAt").exists());

        System.out.println("✓ Task created timestamp test passed");
    }

    // ==================== RELATIONSHIP TESTS ====================

    @Test
    public void testUserHasTasksRelationship() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tasks").isArray());

        System.out.println("✓ User-Task relationship test passed");
    }

    @Test
    public void testTaskBelongsToUser() throws Exception {
        String taskJson = "{"
            + "\"title\": \"Relationship Task\","
            + "\"description\": \"Test\","
            + "\"dueDate\": \"2026-05-25\","
            + "\"userId\": 1"
            + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(1));

        System.out.println("✓ Task belongs to user test passed");
    }

}
