package com.mindtogether.goals.service;

import com.mindtogether.goals.model.Achievement;
import com.mindtogether.goals.model.AchievementType;
import com.mindtogether.goals.model.GoalCategory;
import com.mindtogether.goals.model.MentalHealthGoal;
import com.mindtogether.goals.repository.AchievementRepository;
import com.mindtogether.goals.repository.GoalMilestoneRepository;
import com.mindtogether.goals.repository.MentalHealthGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final MentalHealthGoalRepository goalRepository;
    private final GoalMilestoneRepository milestoneRepository;

    // ==================== Query Methods ====================

    public List<Achievement> getAllAchievements(String userId) {
        return achievementRepository.findByUserIdOrderByUnlockedAtDesc(userId);
    }

    public List<Achievement> getRecentAchievements(String userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return achievementRepository.findRecentAchievements(userId, since);
    }

    public Long getTotalAchievements(String userId) {
        return achievementRepository.countByUserId(userId);
    }

    public Long getTotalPoints(String userId) {
        Long points = achievementRepository.getTotalPointsByUser(userId);
        return points != null ? points : 0L;
    }

    public List<Achievement> getFeaturedAchievements(String userId) {
        return achievementRepository.findByUserIdAndIsFeaturedTrue(userId);
    }

    public boolean hasAchievement(String userId, AchievementType type) {
        return achievementRepository.existsByUserIdAndAchievementType(userId, type);
    }

    // ==================== Unlock Achievement ====================

    @Transactional
    public Optional<Achievement> unlockAchievement(String userId, AchievementType type, Long relatedGoalId) {
        // Verificar si ya tiene el logro
        if (hasAchievement(userId, type)) {
            log.debug("User {} already has achievement {}", userId, type);
            return Optional.empty();
        }

        Achievement achievement = Achievement.builder()
                .userId(userId)
                .achievementType(type)
                .relatedGoalId(relatedGoalId)
                .pointsEarned(type.getPoints())
                .isFeatured(isHighValueAchievement(type))
                .notificationSent(false)
                .build();

        Achievement saved = achievementRepository.save(achievement);
        log.info("🏆 Achievement unlocked: {} for user {} ({} points)", 
                 type.getDisplayName(), userId, type.getPoints());
        
        return Optional.of(saved);
    }

    private boolean isHighValueAchievement(AchievementType type) {
        // Considera logros de alto valor para destacar
        return type.getPoints() >= 100;
    }

    // ==================== Achievement Checks ====================

    @Transactional
    public void checkFirstGoalCreated(String userId) {
        Long totalGoals = goalRepository.countByUserId(userId);
        if (totalGoals == 1) {
            unlockAchievement(userId, AchievementType.FIRST_GOAL_CREATED, null);
        }
    }

    @Transactional
    public void checkFirstMilestone(String userId) {
        Long completedMilestones = milestoneRepository.findAllCompletedByUser(userId).stream().count();
        if (completedMilestones == 1) {
            unlockAchievement(userId, AchievementType.FIRST_MILESTONE, null);
        }
    }

    @Transactional
    public void checkGoalCompletionAchievements(String userId, MentalHealthGoal goal) {
        Long completedGoals = goalRepository.countByUserIdAndStatus(userId, MentalHealthGoal.GoalStatus.COMPLETED);
        
        // Primer meta completada
        if (completedGoals == 1) {
            unlockAchievement(userId, AchievementType.FIRST_GOAL_COMPLETED, goal.getId());
        }
        
        // Metas por cantidad
        switch (completedGoals.intValue()) {
            case 5 -> unlockAchievement(userId, AchievementType.GOALS_COMPLETED_5, goal.getId());
            case 10 -> unlockAchievement(userId, AchievementType.GOALS_COMPLETED_10, goal.getId());
            case 25 -> unlockAchievement(userId, AchievementType.GOALS_COMPLETED_25, goal.getId());
            case 50 -> unlockAchievement(userId, AchievementType.GOALS_COMPLETED_50, goal.getId());
            case 100 -> unlockAchievement(userId, AchievementType.GOALS_COMPLETED_100, goal.getId());
        }
        
        // Maestro de categoría (10 metas de la misma categoría)
        Long completedInCategory = goalRepository.countCompletedByCategory(userId, goal.getCategory());
        if (completedInCategory == 10) {
            checkCategoryMasterAchievement(userId, goal.getCategory(), goal.getId());
        }
        
        // Completado antes de tiempo
        if (goal.isCompletedEarly()) {
            unlockAchievement(userId, AchievementType.OVERACHIEVER, goal.getId());
        }
        
        // Verificar rachas (se implementaría con lógica más compleja)
        checkStreakAchievements(userId);
    }

    @Transactional
    protected void checkCategoryMasterAchievement(String userId, GoalCategory category, Long goalId) {
        AchievementType achievementType = switch (category) {
            case MINDFULNESS -> AchievementType.MINDFULNESS_MASTER;
            case PHYSICAL_ACTIVITY -> AchievementType.FITNESS_CHAMPION;
            case SOCIAL_CONNECTION -> AchievementType.SOCIAL_BUTTERFLY;
            case EMOTIONAL_REGULATION -> AchievementType.EMOTIONAL_WARRIOR;
            case SLEEP_QUALITY -> AchievementType.SLEEP_EXPERT;
            case CREATIVE_EXPRESSION -> AchievementType.CREATIVE_SOUL;
            default -> null;
        };
        
        if (achievementType != null) {
            unlockAchievement(userId, achievementType, goalId);
        }
    }

    @Transactional
    public void checkProgressAchievements(String userId, MentalHealthGoal goal) {
        // Quick starter: 25% en 3 días
        if (goal.getProgressPercentage() >= 25.0 && goal.getDaysActive() <= 3) {
            unlockAchievement(userId, AchievementType.QUICK_STARTER, goal.getId());
        }
        
        // Halfway hero: 50% en 10 metas diferentes
        List<MentalHealthGoal> activeGoals = goalRepository.findByUserIdAndStatus(userId, MentalHealthGoal.GoalStatus.ACTIVE);
        long halfwayGoals = activeGoals.stream()
                .filter(g -> g.getProgressPercentage() >= 50.0)
                .count();
        
        if (halfwayGoals >= 10) {
            unlockAchievement(userId, AchievementType.HALFWAY_HERO, null);
        }
        
        // Consistent: 3 metas activas simultáneamente
        if (activeGoals.size() >= 3) {
            unlockAchievement(userId, AchievementType.CONSISTENT, null);
        }
    }

    @Transactional
    protected void checkStreakAchievements(String userId) {
        // TODO: Implementar lógica de rachas basada en lastProgressDate
        // Por ahora es un placeholder
        
        List<MentalHealthGoal> recentProgress = goalRepository.findWithProgressSince(
                userId, 
                java.time.LocalDate.now().minusDays(7)
        );
        
        if (recentProgress.size() >= 7) {
            unlockAchievement(userId, AchievementType.STREAK_7_DAYS, null);
        }
    }

    // ==================== Feature/Share Management ====================

    @Transactional
    public Optional<Achievement> toggleFeatured(Long achievementId, String userId) {
        return achievementRepository.findById(achievementId)
                .filter(achievement -> achievement.getUserId().equals(userId))
                .map(achievement -> {
                    achievement.setIsFeatured(!achievement.getIsFeatured());
                    return achievementRepository.save(achievement);
                });
    }

    @Transactional
    public Optional<Achievement> shareWithCircles(Long achievementId, String userId) {
        return achievementRepository.findById(achievementId)
                .filter(achievement -> achievement.getUserId().equals(userId))
                .map(achievement -> {
                    achievement.setSharedWithCircles(true);
                    return achievementRepository.save(achievement);
                });
    }

    @Transactional
    public void markNotificationsSent(String userId) {
        List<Achievement> pending = achievementRepository.findByUserIdAndNotificationSentFalse(userId);
        for (Achievement achievement : pending) {
            achievement.setNotificationSent(true);
            achievementRepository.save(achievement);
        }
    }
}

