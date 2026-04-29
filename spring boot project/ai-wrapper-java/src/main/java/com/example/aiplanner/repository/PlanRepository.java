package com.example.aiplanner.repository;

import com.shay.ai_wrapper_java.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {

}
