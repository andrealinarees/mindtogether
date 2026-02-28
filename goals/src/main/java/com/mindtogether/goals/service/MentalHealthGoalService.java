package com.mindtogether.goals.service;

import com.mindtogether.goals.model.*;
import com.mindtogether.goals.repository.GoalMilestoneRepository;
import com.mindtogether.goals.repository.MentalHealthGoalRepository;
import com.mindtogether.goals.repository.ProgressSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentalHealthGoalService {

    private final MentalHealthGoalRepository goalRepository;
    private final GoalMilestoneRepository milestoneRepository;
    private final ProgressSnapshotRepository snapshotRepository;
    private final CustomRewardService customRewardService;

    // ==================== CRUD Operations ====================

    public List<MentalHealthGoal> getAllGoals(String userId) {
        return goalRepository.findByUserId(userId);
    }

    public List<MentalHealthGoal> getActiveGoals(String userId) {
        return goalRepository.findActiveGoalsOrderByDate(userId);
    }

    public List<MentalHealthGoal> getGoalsByStatus(String userId, MentalHealthGoal.GoalStatus status) {
        return goalRepository.findByUserIdAndStatus(userId, status);
    }

    public List<MentalHealthGoal> getGoalsByCategory(String userId, GoalCategory category) {
        return goalRepository.findByUserIdAndCategory(userId, category);
    }

    public List<MentalHealthGoal> getOverdueGoals(String userId) {
        return goalRepository.findOverdueGoals(userId, LocalDate.now());
    }

    public List<MentalHealthGoal> searchGoals(String userId, String searchTerm) {
        return goalRepository.searchByUserIdAndNameOrDescription(userId, searchTerm);
    }

    public Optional<MentalHealthGoal> getGoalById(Long id, String userId) {
        return goalRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public MentalHealthGoal createGoal(MentalHealthGoal goal, String userId) {
        log.info("Creating mental health goal for user: {}, category: {}", userId, goal.getCategory());
        
        goal.setUserId(userId);
        goal.setStatus(MentalHealthGoal.GoalStatus.ACTIVE);
        goal.setCurrentProgress(0);
        
        MentalHealthGoal savedGoal = goalRepository.save(goal);
        
        return savedGoal;
    }

    @Transactional
    public Optional<MentalHealthGoal> updateGoal(Long id, MentalHealthGoal updatedGoal, String userId) {
        return goalRepository.findByIdAndUserId(id, userId)
                .map(goal -> {
                    goal.setName(updatedGoal.getName());
                    goal.setDescription(updatedGoal.getDescription());
                    goal.setCategory(updatedGoal.getCategory());
                    goal.setTargetValue(updatedGoal.getTargetValue());
                    goal.setUnit(updatedGoal.getUnit());
                    goal.setTargetDate(updatedGoal.getTargetDate());
                    goal.setPersonalMotivation(updatedGoal.getPersonalMotivation());
                    goal.setExpectedBenefit(updatedGoal.getExpectedBenefit());
                    goal.setRewardDescription(updatedGoal.getRewardDescription());
                    goal.setReminderEnabled(updatedGoal.getReminderEnabled());
                    goal.setReminderTime(updatedGoal.getReminderTime());
                    goal.setDifficultyLevel(updatedGoal.getDifficultyLevel());
                    goal.setIsSharedWithCircles(updatedGoal.getIsSharedWithCircles());
                    
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public boolean deleteGoal(Long id, String userId) {
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(id, userId);
        
        if (goal.isEmpty()) {
            return false;
        }
        
        goalRepository.deleteById(id);
        log.info("Deleted goal {} for user {}", id, userId);
        return true;
    }

    // ==================== Progress Management ====================

    @Transactional
    public Optional<MentalHealthGoal> updateProgress(Long id, Integer newProgress, String userId, Integer moodRating) {
        return goalRepository.findByIdAndUserId(id, userId)
                .map(goal -> {
                    int oldProgress = goal.getCurrentProgress();
                    goal.setProgress(newProgress);
                    
                    // Crear snapshot del progreso
                    createProgressSnapshot(goal, moodRating, oldProgress < newProgress);
                    
                    // Verificar si completó la meta
                    if (newProgress >= goal.getTargetValue() && goal.getStatus() == MentalHealthGoal.GoalStatus.ACTIVE) {
                        completeGoal(goal);
                    }
                    
                    MentalHealthGoal savedGoal = goalRepository.save(goal);
                    
                    return savedGoal;
                });
    }

    @Transactional
    public Optional<MentalHealthGoal> incrementProgress(Long id, Integer amount, String userId, Integer moodRating) {
        return goalRepository.findByIdAndUserId(id, userId)
                .map(goal -> {
                    goal.incrementProgress(amount);
                    
                    // Crear snapshot del progreso
                    createProgressSnapshot(goal, moodRating, true);
                    
                    // Verificar hitos
                    checkMilestones(goal);
                    
                    // Verificar si completó la meta
                    if (goal.getCurrentProgress() >= goal.getTargetValue() && 
                        goal.getStatus() == MentalHealthGoal.GoalStatus.ACTIVE) {
                        completeGoal(goal);
                    }
                    
                    MentalHealthGoal savedGoal = goalRepository.save(goal);
                    
                    return savedGoal;
                });
    }

    @Transactional
    protected void completeGoal(MentalHealthGoal goal) {
        goal.setStatus(MentalHealthGoal.GoalStatus.COMPLETED);
        goal.setCompletedAt(LocalDateTime.now());
        
        log.info("Goal {} completed by user {}. Completed in {} days.", 
                 goal.getId(), goal.getUserId(), goal.getDaysActive());
        
        // Desbloquear recompensas asociadas a esta meta
        try {
            customRewardService.unlockRewardsForMentalHealthGoal(goal.getId(), goal.getUserId());
        } catch (Exception e) {
            log.warn("Error unlocking rewards for MH goal {}: {}", goal.getId(), e.getMessage());
        }
    }

    @Transactional
    public void checkAndUpdateOverdueGoals(String userId) {
        List<MentalHealthGoal> overdueGoals = getOverdueGoals(userId);
        
        for (MentalHealthGoal goal : overdueGoals) {
            if (goal.getCurrentProgress() >= goal.getTargetValue()) {
                completeGoal(goal);
            } else {
                goal.setStatus(MentalHealthGoal.GoalStatus.FAILED);
                log.info("Goal {} marked as FAILED for user {}", goal.getId(), userId);
            }
            goalRepository.save(goal);
        }
    }

    // ==================== Milestone Management ====================

    @Transactional
    public GoalMilestone addMilestone(Long goalId, GoalMilestone milestone, String userId) {
        Optional<MentalHealthGoal> goalOpt = goalRepository.findByIdAndUserId(goalId, userId);
        
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("Goal not found");
        }
        
        MentalHealthGoal goal = goalOpt.get();
        goal.addMilestone(milestone);
        goalRepository.save(goal);
        
        return milestone;
    }

    public List<GoalMilestone> getMilestones(Long goalId, String userId) {
        // Verificar que la meta pertenece al usuario
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(goalId, userId);
        if (goal.isEmpty()) {
            return List.of();
        }
        
        return milestoneRepository.findByGoalIdOrderByOrderIndexAsc(goalId);
    }

    @Transactional
    protected void checkMilestones(MentalHealthGoal goal) {
        List<GoalMilestone> milestones = milestoneRepository.findByGoalIdOrderByOrderIndexAsc(goal.getId());
        
        for (GoalMilestone milestone : milestones) {
            if (!milestone.getIsCompleted() && goal.getCurrentProgress() >= milestone.getTargetValue()) {
                milestone.markAsCompleted();
                milestoneRepository.save(milestone);
                
                log.info("Milestone {} completed for goal {}", milestone.getId(), goal.getId());
            }
        }
    }

    // ==================== Progress Snapshots ====================

    @Transactional
    protected void createProgressSnapshot(MentalHealthGoal goal, Integer moodRating, boolean wasProgress) {
        // Evitar duplicados en el mismo día
        if (snapshotRepository.existsByGoalIdAndSnapshotDate(goal.getId(), LocalDate.now())) {
            return;
        }
        
        ProgressSnapshot snapshot = ProgressSnapshot.builder()
                .goal(goal)
                .snapshotDate(LocalDate.now())
                .progressValue(goal.getCurrentProgress())
                .progressPercentage(goal.getProgressPercentage())
                .daysActive(goal.getDaysActive())
                .daysRemaining(goal.getDaysRemaining())
                .moodRating(moodRating)
                .milestoneReached(false)
                .build();
        
        snapshotRepository.save(snapshot);
    }

    public List<ProgressSnapshot> getProgressHistory(Long goalId, String userId) {
        // Verificar que la meta pertenece al usuario
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(goalId, userId);
        if (goal.isEmpty()) {
            return List.of();
        }
        
        return snapshotRepository.findByGoalIdOrderBySnapshotDateAsc(goalId);
    }

    // ==================== Statistics ====================

    public GoalStatistics getStatistics(String userId) {
        Long totalGoals = goalRepository.countByUserId(userId);
        Long activeGoals = goalRepository.countByUserIdAndStatus(userId, MentalHealthGoal.GoalStatus.ACTIVE);
        Long completedGoals = goalRepository.countByUserIdAndStatus(userId, MentalHealthGoal.GoalStatus.COMPLETED);
        Long failedGoals = goalRepository.countByUserIdAndStatus(userId, MentalHealthGoal.GoalStatus.FAILED);
        
        Double avgProgress = goalRepository.getAverageProgressPercentage(userId);
        if (avgProgress == null) avgProgress = 0.0;
        
        return new GoalStatistics(
                totalGoals, activeGoals, completedGoals, failedGoals,
                avgProgress
        );
    }

    // ==================== Inner Class for Statistics ====================

    public record GoalStatistics(
            Long totalGoals,
            Long activeGoals,
            Long completedGoals,
            Long failedGoals,
            Double averageProgress
    ) {}
}

