package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.ProgressSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressSnapshotRepository extends JpaRepository<ProgressSnapshot, Long> {
    
    List<ProgressSnapshot> findByGoalIdOrderBySnapshotDateAsc(Long goalId);
    
    List<ProgressSnapshot> findByGoalId(Long goalId);
    
    @Query("SELECT s FROM ProgressSnapshot s WHERE s.goal.id = :goalId AND s.snapshotDate BETWEEN :startDate AND :endDate ORDER BY s.snapshotDate ASC")
    List<ProgressSnapshot> findByGoalIdAndDateRange(@Param("goalId") Long goalId, 
                                                      @Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM ProgressSnapshot s WHERE s.goal.userId = :userId AND s.snapshotDate = :date")
    List<ProgressSnapshot> findByUserAndDate(@Param("userId") String userId, @Param("date") LocalDate date);
    
    @Query("SELECT s FROM ProgressSnapshot s WHERE s.goal.userId = :userId AND s.milestoneReached = true ORDER BY s.snapshotDate DESC")
    List<ProgressSnapshot> findMilestoneSnapshotsByUser(@Param("userId") String userId);
    
    Optional<ProgressSnapshot> findTopByGoalIdOrderBySnapshotDateDesc(Long goalId);
    
    @Query("SELECT s FROM ProgressSnapshot s WHERE s.goal.id = :goalId AND s.moodRating IS NOT NULL ORDER BY s.snapshotDate ASC")
    List<ProgressSnapshot> findSnapshotsWithMood(@Param("goalId") Long goalId);
    
    @Query("SELECT AVG(s.moodRating) FROM ProgressSnapshot s WHERE s.goal.userId = :userId AND s.moodRating IS NOT NULL AND s.snapshotDate BETWEEN :startDate AND :endDate")
    Double getAverageMoodRating(@Param("userId") String userId, 
                                 @Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
    
    boolean existsByGoalIdAndSnapshotDate(Long goalId, LocalDate snapshotDate);
}

