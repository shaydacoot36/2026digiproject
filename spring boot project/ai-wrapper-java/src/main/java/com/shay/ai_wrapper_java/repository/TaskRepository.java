package com.shay.ai_wrapper_java.repository;

import com.shay.ai_wrapper_java.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.user.userId = :userId")
    List<Task> findByUserId(@Param("userId") Long userId);
}
