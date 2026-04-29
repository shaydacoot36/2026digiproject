package com.example.aiplanner.repository;

import com.shay.ai_wrapper_java.model.Plan;
import com.shay.ai_wrapper_java.AiWrapperJavaApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AiWrapperJavaApplication.class)
@ActiveProfiles("dev")
public class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Test
    public void testSaveAndFindPlan() {
        // Create and save a plan
        Plan plan = new Plan();
        plan.setTitle("Test Plan");
        plan.setDescription("This is a test plan");
        
        Plan savedPlan = planRepository.save(plan);
        
        assertNotNull(savedPlan.getId(), "Plan ID should be generated");
        assertEquals("Test Plan", savedPlan.getTitle(), "Title should match");
        assertEquals("This is a test plan", savedPlan.getDescription(), "Description should match");
        assertNotNull(savedPlan.getCreatedAt(), "CreatedAt should be auto-set");
    }

    @Test
    public void testFindPlanById() {
        // Create and save a plan
        Plan plan = new Plan();
        plan.setTitle("Find Me Plan");
        plan.setDescription("A plan to find");
        Plan savedPlan = planRepository.save(plan);
        
        // Find by ID
        var foundPlan = planRepository.findById(savedPlan.getId());
        
        assertTrue(foundPlan.isPresent(), "Plan should be found by ID");
        assertEquals("Find Me Plan", foundPlan.get().getTitle(), "Title should match");
    }

    @Test
    public void testFindAllPlans() {
        // Create and save multiple plans
        Plan plan1 = new Plan();
        plan1.setTitle("Plan 1");
        plan1.setDescription("Description 1");
        planRepository.save(plan1);
        
        Plan plan2 = new Plan();
        plan2.setTitle("Plan 2");
        plan2.setDescription("Description 2");
        planRepository.save(plan2);
        
        // Find all
        var allPlans = planRepository.findAll();
        
        assertTrue(allPlans.size() >= 2, "Should have at least 2 plans");
    }
}
