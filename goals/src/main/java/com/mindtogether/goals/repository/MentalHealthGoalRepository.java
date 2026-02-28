package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.GoalCategory;
import com.mindtogether.goals.model.MentalHealthGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MentalHealthGoalRepository extends JpaRepository<MentalHealthGoal, Long> {
    
    List<MentalHealthGoal> findByUserId(String userId);
    
    List<MentalHealthGoal> findByUserIdAndStatus(String userId, MentalHealthGoal.GoalStatus status);
    
    List<MentalHealthGoal> findByUserIdAndCategory(String userId, GoalCategory category);
    
    List<MentalHealthGoal> findByUserIdAndWellnessPracticeId(String userId, Long wellnessPracticeId);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND g.status = 'ACTIVE' ORDER BY g.targetDate ASC")
    List<MentalHealthGoal> findActiveGoalsOrderByDate(@Param("userId") String userId);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND g.status = 'ACTIVE' AND g.targetDate < :today")
    List<MentalHealthGoal> findOverdueGoals(@Param("userId") String userId, @Param("today") LocalDate today);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND g.targetDate BETWEEN :startDate AND :endDate")
    List<MentalHealthGoal> findByUserIdAndDateRange(@Param("userId") String userId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(g) FROM MentalHealthGoal g WHERE g.userId = :userId AND g.status = :status")
    Long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") MentalHealthGoal.GoalStatus status);
    
    @Query("SELECT COUNT(g) FROM MentalHealthGoal g WHERE g.userId = :userId AND g.category = :category AND g.status = 'COMPLETED'")
    Long countCompletedByCategory(@Param("userId") String userId, @Param("category") GoalCategory category);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<MentalHealthGoal> searchByUserIdAndNameOrDescription(@Param("userId") String userId, 
                                                                @Param("searchTerm") String searchTerm);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND g.isSharedWithCircles = true")
    List<MentalHealthGoal> findSharedGoals(@Param("userId") String userId);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND " +
           "g.status = 'ACTIVE' AND g.reminderEnabled = true")
    List<MentalHealthGoal> findGoalsWithReminders(@Param("userId") String userId);
    
    @Query("SELECT g FROM MentalHealthGoal g WHERE g.userId = :userId AND " +
           "g.lastProgressDate IS NOT NULL AND g.lastProgressDate >= :date")
    List<MentalHealthGoal> findWithProgressSince(@Param("userId") String userId, @Param("date") LocalDate date);
    
    // Estadísticas
    @Query("SELECT AVG(g.currentProgress * 100.0 / g.targetValue) FROM MentalHealthGoal g " +
           "WHERE g.userId = :userId AND g.status = 'ACTIVE'")
    Double getAverageProgressPercentage(@Param("userId") String userId);
    
    @Query("SELECT g.category, COUNT(g) FROM MentalHealthGoal g " +
           "WHERE g.userId = :userId AND g.status = 'COMPLETED' " +
           "GROUP BY g.category")
    List<Object[]> getCompletedGoalsByCategory(@Param("userId") String userId);
    
    Long countByUserId(String userId);
    
    Optional<MentalHealthGoal> findByIdAndUserId(Long id, String userId);
}

