package com.mindtogether.goals.repository;

import com.mindtogether.goals.model.CustomReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRewardRepository extends JpaRepository<CustomReward, Long> {

    List<CustomReward> findByUserIdOrderByCreatedAtDesc(String userId);

    List<CustomReward> findByUserIdAndStatus(String userId, CustomReward.RewardStatus status);

    List<CustomReward> findByUserIdAndCategory(String userId, CustomReward.RewardCategory category);

    List<CustomReward> findByGoalId(Long goalId);

    List<CustomReward> findByUserIdAndGoalId(String userId, Long goalId);

    List<CustomReward> findByUserIdAndMentalHealthGoalId(String userId, Long mentalHealthGoalId);

    List<CustomReward> findByMentalHealthGoalId(Long mentalHealthGoalId);

    @Query("SELECT COUNT(r) FROM CustomReward r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(r) FROM CustomReward r WHERE r.userId = :userId AND r.status = :status")
    Long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") CustomReward.RewardStatus status);

    @Query("SELECT r FROM CustomReward r WHERE r.userId = :userId AND r.status <> 'LOCKED' ORDER BY r.unlockedAt DESC")
    List<CustomReward> findUnlockedByUser(@Param("userId") String userId);
}
