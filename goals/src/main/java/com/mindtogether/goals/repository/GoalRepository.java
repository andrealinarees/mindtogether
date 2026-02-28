package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByUserId(String userId);
    
    List<Goal> findByUserIdAndStatus(String userId, Goal.GoalStatus status);
    
    List<Goal> findByUserIdAndHabitId(String userId, Long habitId);
    
    List<Goal> findByHabitId(Long habitId);
    
    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.targetDate BETWEEN :startDate AND :endDate")
    List<Goal> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.status = 'ACTIVE' AND g.targetDate < :today")
    List<Goal> findOverdueGoals(String userId, LocalDate today);
    
    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.status = 'ACTIVE' ORDER BY g.targetDate ASC")
    List<Goal> findActiveGoalsOrderByDate(String userId);
    
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.userId = :userId AND g.status = :status")
    Long countByUserIdAndStatus(String userId, Goal.GoalStatus status);
    
    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Goal> searchByUserIdAndNameOrDescription(String userId, String searchTerm);
}

