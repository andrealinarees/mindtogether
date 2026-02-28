package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.Achievement;
import com.mindtogether.goals.model.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    
    List<Achievement> findByUserIdOrderByUnlockedAtDesc(String userId);
    
    Optional<Achievement> findByUserIdAndAchievementType(String userId, AchievementType achievementType);
    
    boolean existsByUserIdAndAchievementType(String userId, AchievementType achievementType);
    
    List<Achievement> findByUserIdAndIsFeaturedTrue(String userId);
    
    @Query("SELECT a FROM Achievement a WHERE a.userId = :userId AND a.unlockedAt >= :since ORDER BY a.unlockedAt DESC")
    List<Achievement> findRecentAchievements(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(a.pointsEarned) FROM Achievement a WHERE a.userId = :userId")
    Long getTotalPointsByUser(@Param("userId") String userId);
    
    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.userId = :userId")
    Long countByUserId(@Param("userId") String userId);
    
    List<Achievement> findByUserIdAndNotificationSentFalse(String userId);
    
    @Query("SELECT a FROM Achievement a WHERE a.userId = :userId AND a.sharedWithCircles = true")
    List<Achievement> findSharedAchievements(@Param("userId") String userId);
}

