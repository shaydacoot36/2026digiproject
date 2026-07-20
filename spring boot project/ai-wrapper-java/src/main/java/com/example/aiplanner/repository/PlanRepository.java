package com.example.aiplanner.repository;

import com.example.aiplanner.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("aiPlannerPlanRepository")
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByTitleContainingIgnoreCase(String title);
}
