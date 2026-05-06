package com.shay.ai_wrapper_java.repository;

import com.shay.ai_wrapper_java.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
