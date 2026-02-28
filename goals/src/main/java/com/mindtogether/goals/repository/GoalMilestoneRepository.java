package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.GoalMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalMilestoneRepository extends JpaRepository<GoalMilestone, Long> {
    
    List<GoalMilestone> findByGoalIdOrderByOrderIndexAsc(Long goalId);
    
    List<GoalMilestone> findByGoalIdAndIsCompleted(Long goalId, Boolean isCompleted);
    
    @Query("SELECT m FROM GoalMilestone m WHERE m.goal.id = :goalId AND m.isCompleted = true ORDER BY m.completedAt DESC")
    List<GoalMilestone> findCompletedMilestonesOrderByDate(@Param("goalId") Long goalId);
    
    @Query("SELECT COUNT(m) FROM GoalMilestone m WHERE m.goal.id = :goalId AND m.isCompleted = true")
    Long countCompletedMilestones(@Param("goalId") Long goalId);
    
    @Query("SELECT COUNT(m) FROM GoalMilestone m WHERE m.goal.id = :goalId")
    Long countTotalMilestones(@Param("goalId") Long goalId);
    
    @Query("SELECT m FROM GoalMilestone m WHERE m.goal.userId = :userId AND m.isCompleted = true")
    List<GoalMilestone> findAllCompletedByUser(@Param("userId") String userId);
}

