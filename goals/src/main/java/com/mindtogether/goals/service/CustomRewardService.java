package com.mindtogether.goals.service;

import com.mindtogether.goals.model.CustomReward;
import com.mindtogether.goals.model.Goal;
import com.mindtogether.goals.model.MentalHealthGoal;
import com.mindtogether.goals.repository.CustomRewardRepository;
import com.mindtogether.goals.repository.GoalRepository;
import com.mindtogether.goals.repository.MentalHealthGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomRewardService {

    private final CustomRewardRepository rewardRepository;
    private final GoalRepository goalRepository;
    private final MentalHealthGoalRepository mentalHealthGoalRepository;

    // ==================== Query Methods ====================

    public List<CustomReward> getAllRewards(String userId) {
        List<CustomReward> rewards = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId);
        populateGoalNames(rewards);
        return rewards;
    }

    public List<CustomReward> getRewardsByStatus(String userId, CustomReward.RewardStatus status) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndStatus(userId, status);
        populateGoalNames(rewards);
        return rewards;
    }

    public List<CustomReward> getRewardsByCategory(String userId, CustomReward.RewardCategory category) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndCategory(userId, category);
        populateGoalNames(rewards);
        return rewards;
    }

    public List<CustomReward> getRewardsByGoal(String userId, Long goalId) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndGoalId(userId, goalId);
        populateGoalNames(rewards);
        return rewards;
    }

    public List<CustomReward> getRewardsByMentalHealthGoal(String userId, Long mentalHealthGoalId) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndMentalHealthGoalId(userId, mentalHealthGoalId);
        populateGoalNames(rewards);
        return rewards;
    }

    public List<CustomReward> getUnlockedRewards(String userId) {
        List<CustomReward> rewards = rewardRepository.findUnlockedByUser(userId);
        populateGoalNames(rewards);
        return rewards;
    }

    public Optional<CustomReward> getRewardById(Long id, String userId) {
        return rewardRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .map(r -> {
                    populateGoalName(r);
                    return r;
                });
    }

    public Map<String, Object> getRewardStats(String userId) {
        Long total = rewardRepository.countByUserId(userId);
        Long locked = rewardRepository.countByUserIdAndStatus(userId, CustomReward.RewardStatus.LOCKED);
        Long unlocked = rewardRepository.countByUserIdAndStatus(userId, CustomReward.RewardStatus.UNLOCKED);
        Long claimed = rewardRepository.countByUserIdAndStatus(userId, CustomReward.RewardStatus.CLAIMED);

        return Map.of(
                "total", total,
                "locked", locked,
                "unlocked", unlocked,
                "claimed", claimed
        );
    }

    // ==================== Create / Update / Delete ====================

    @Transactional
    public CustomReward createReward(CustomReward reward, String userId) {
        reward.setUserId(userId);
        reward.setStatus(CustomReward.RewardStatus.LOCKED);

        // Verificar meta básica (Goal) si se proporciona goalId
        if (reward.getGoalId() != null) {
            Optional<Goal> goal = goalRepository.findById(reward.getGoalId())
                    .filter(g -> g.getUserId().equals(userId));
            if (goal.isEmpty()) {
                throw new IllegalArgumentException("La meta no existe o no pertenece al usuario");
            }
            reward.setGoalName(goal.get().getName());

            if (goal.get().getStatus() == Goal.GoalStatus.COMPLETED) {
                reward.setStatus(CustomReward.RewardStatus.UNLOCKED);
                reward.setUnlockedAt(LocalDateTime.now());
                log.info("🎁 Reward '{}' auto-unlocked (basic goal already completed)", reward.getName());
            }
        }

        // Verificar meta de salud mental (MentalHealthGoal) si se proporciona mentalHealthGoalId
        if (reward.getMentalHealthGoalId() != null) {
            Optional<MentalHealthGoal> mhGoal = mentalHealthGoalRepository.findByIdAndUserId(
                    reward.getMentalHealthGoalId(), userId);
            if (mhGoal.isEmpty()) {
                throw new IllegalArgumentException("La meta de salud mental no existe o no pertenece al usuario");
            }
            reward.setGoalName(mhGoal.get().getName());

            if (mhGoal.get().getStatus() == MentalHealthGoal.GoalStatus.COMPLETED) {
                reward.setStatus(CustomReward.RewardStatus.UNLOCKED);
                reward.setUnlockedAt(LocalDateTime.now());
                log.info("🎁 Reward '{}' auto-unlocked (MH goal already completed)", reward.getName());
            }
        }

        CustomReward saved = rewardRepository.save(reward);
        log.info("🎁 Custom reward created: '{}' for user {}", saved.getName(), userId);
        return saved;
    }

    @Transactional
    public Optional<CustomReward> updateReward(Long id, CustomReward updatedReward, String userId) {
        return rewardRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .map(reward -> {
                    reward.setName(updatedReward.getName());
                    reward.setDescription(updatedReward.getDescription());
                    reward.setIcon(updatedReward.getIcon());
                    reward.setCategory(updatedReward.getCategory());

                    // Permitir cambiar la meta asociada solo si está LOCKED
                    if (reward.getStatus() == CustomReward.RewardStatus.LOCKED) {
                        if (updatedReward.getGoalId() != null) {
                            Optional<Goal> goal = goalRepository.findById(updatedReward.getGoalId())
                                    .filter(g -> g.getUserId().equals(userId));
                            if (goal.isPresent()) {
                                reward.setGoalId(updatedReward.getGoalId());
                                reward.setMentalHealthGoalId(null);
                                reward.setGoalName(goal.get().getName());
                            }
                        }
                        if (updatedReward.getMentalHealthGoalId() != null) {
                            Optional<MentalHealthGoal> mhGoal = mentalHealthGoalRepository.findByIdAndUserId(
                                    updatedReward.getMentalHealthGoalId(), userId);
                            if (mhGoal.isPresent()) {
                                reward.setMentalHealthGoalId(updatedReward.getMentalHealthGoalId());
                                reward.setGoalId(null);
                                reward.setGoalName(mhGoal.get().getName());
                            }
                        }
                    }

                    return rewardRepository.save(reward);
                });
    }

    @Transactional
    public boolean deleteReward(Long id, String userId) {
        Optional<CustomReward> reward = rewardRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId));
        if (reward.isEmpty()) {
            return false;
        }
        rewardRepository.deleteById(id);
        log.info("🗑️ Custom reward deleted: id={} for user {}", id, userId);
        return true;
    }

    // ==================== Unlock / Claim ====================

    /**
     * Se llama cuando una meta básica (Goal) se completa.
     */
    @Transactional
    public List<CustomReward> unlockRewardsForGoal(Long goalId, String userId) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndGoalId(userId, goalId);
        return unlockRewards(rewards, userId, "goal " + goalId);
    }

    /**
     * Se llama cuando una meta de salud mental (MentalHealthGoal) se completa.
     */
    @Transactional
    public List<CustomReward> unlockRewardsForMentalHealthGoal(Long mentalHealthGoalId, String userId) {
        List<CustomReward> rewards = rewardRepository.findByUserIdAndMentalHealthGoalId(userId, mentalHealthGoalId);
        return unlockRewards(rewards, userId, "MH goal " + mentalHealthGoalId);
    }

    private List<CustomReward> unlockRewards(List<CustomReward> rewards, String userId, String goalDesc) {
        LocalDateTime now = LocalDateTime.now();

        for (CustomReward reward : rewards) {
            if (reward.getStatus() == CustomReward.RewardStatus.LOCKED) {
                reward.setStatus(CustomReward.RewardStatus.UNLOCKED);
                reward.setUnlockedAt(now);
                rewardRepository.save(reward);
                log.info("🎁 Reward '{}' unlocked for user {} ({} completed)",
                        reward.getName(), userId, goalDesc);
            }
        }

        return rewards.stream()
                .filter(r -> r.getStatus() == CustomReward.RewardStatus.UNLOCKED)
                .toList();
    }

    /**
     * El usuario marca la recompensa como reclamada (se la ha dado a sí mismo).
     */
    @Transactional
    public Optional<CustomReward> claimReward(Long id, String userId) {
        return rewardRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .filter(r -> r.getStatus() == CustomReward.RewardStatus.UNLOCKED)
                .map(reward -> {
                    reward.setStatus(CustomReward.RewardStatus.CLAIMED);
                    reward.setClaimedAt(LocalDateTime.now());
                    log.info("🎉 Reward '{}' claimed by user {}", reward.getName(), userId);
                    return rewardRepository.save(reward);
                });
    }

    // ==================== Helpers ====================

    private void populateGoalNames(List<CustomReward> rewards) {
        rewards.forEach(this::populateGoalName);
    }

    private void populateGoalName(CustomReward reward) {
        if (reward.getGoalName() != null) return;
        
        if (reward.getGoalId() != null) {
            goalRepository.findById(reward.getGoalId())
                    .ifPresent(goal -> reward.setGoalName(goal.getName()));
        }
        if (reward.getGoalName() == null && reward.getMentalHealthGoalId() != null) {
            mentalHealthGoalRepository.findById(reward.getMentalHealthGoalId())
                    .ifPresent(goal -> reward.setGoalName(goal.getName()));
        }
    }
}
