package es.udc.asi.goals.service;

import es.udc.asi.goals.client.HabitDTO;
import es.udc.asi.goals.client.HabitsClient;
import es.udc.asi.goals.model.Goal;
import es.udc.asi.goals.repository.GoalRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final HabitsClient habitsClient;

    public List<Goal> getAllGoals(String userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        populateHabitNames(goals);
        return goals;
    }

    public List<Goal> getActiveGoals(String userId) {
        List<Goal> goals = goalRepository.findActiveGoalsOrderByDate(userId);
        populateHabitNames(goals);
        return goals;
    }

    public List<Goal> getGoalsByStatus(String userId, Goal.GoalStatus status) {
        List<Goal> goals = goalRepository.findByUserIdAndStatus(userId, status);
        populateHabitNames(goals);
        return goals;
    }

    public List<Goal> getGoalsByHabit(String userId, Long habitId) {
        List<Goal> goals = goalRepository.findByUserIdAndHabitId(userId, habitId);
        populateHabitNames(goals);
        return goals;
    }

    public List<Goal> getOverdueGoals(String userId) {
        List<Goal> goals = goalRepository.findOverdueGoals(userId, LocalDate.now());
        populateHabitNames(goals);
        return goals;
    }

    public List<Goal> searchGoals(String userId, String searchTerm) {
        List<Goal> goals = goalRepository.searchByUserIdAndNameOrDescription(userId, searchTerm);
        populateHabitNames(goals);
        return goals;
    }

    public Optional<Goal> getGoalById(Long id, String userId) {
        Optional<Goal> goalOpt = goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId));
        goalOpt.ifPresent(goal -> {
            if (goal.getHabitName() == null) {
                populateHabitName(goal);
            }
        });
        return goalOpt;
    }
    
    /**
     * Pobla el nombre del hábito en cada meta si no está presente
     */
    private void populateHabitNames(List<Goal> goals) {
        goals.forEach(this::populateHabitName);
    }
    
    /**
     * Obtiene el nombre del hábito y lo guarda en la meta si no está presente
     */
    private void populateHabitName(Goal goal) {
        if (goal.getHabitName() == null && goal.getHabitId() != null) {
            try {
                HabitDTO habit = habitsClient.getHabitById(goal.getHabitId(), goal.getUserId());
                if (habit != null) {
                    goal.setHabitName(habit.getName());
                }
            } catch (Exception e) {
                log.warn("Could not fetch habit name for habitId {}: {}", goal.getHabitId(), e.getMessage());
            }
        }
    }

    @Transactional
    public Goal createGoal(Goal goal, String userId) {
        log.info("Creating goal for user: {} and habit: {}", userId, goal.getHabitId());
        
        // Verificar que el hábito existe y pertenece al usuario
        try {
            HabitDTO habit = habitsClient.getHabitById(goal.getHabitId(), userId);
            if (habit == null) {
                throw new IllegalArgumentException("Habit not found or does not belong to user");
            }
            log.info("Habit validated: {}", habit.getName());
            // Guardar el nombre del hábito para mostrarlo en la vista
            goal.setHabitName(habit.getName());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Habit not found");
        } catch (FeignException e) {
            log.warn("Error calling habits service: {}", e.getMessage());
            // Continuar sin validación si el servicio no está disponible
        }
        
        goal.setUserId(userId);
        goal.setStatus(Goal.GoalStatus.ACTIVE);
        goal.setCurrentProgress(0);
        
        return goalRepository.save(goal);
    }

    @Transactional
    public Optional<Goal> updateGoal(Long id, Goal updatedGoal, String userId) {
        return goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId))
                .map(goal -> {
                    goal.setName(updatedGoal.getName());
                    goal.setDescription(updatedGoal.getDescription());
                    goal.setTargetValue(updatedGoal.getTargetValue());
                    goal.setTargetDate(updatedGoal.getTargetDate());
                    goal.setPersonalReward(updatedGoal.getPersonalReward());
                    
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public boolean deleteGoal(Long id, String userId) {
        Optional<Goal> goal = goalRepository.findById(id)
                .filter(g -> g.getUserId().equals(userId));
        
        if (goal.isEmpty()) {
            return false;
        }
        
        goalRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Goal> updateProgress(Long id, Integer newProgress, String userId) {
        return goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId))
                .map(goal -> {
                    goal.setCurrentProgress(newProgress);
                    
                    // Si alcanzó el objetivo, marcarlo como completado
                    if (newProgress >= goal.getTargetValue() && goal.getStatus() == Goal.GoalStatus.ACTIVE) {
                        goal.setStatus(Goal.GoalStatus.COMPLETED);
                        goal.setCompletedAt(java.time.LocalDateTime.now());
                        log.info("Goal {} completed by user {}", id, userId);
                    }
                    
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public Optional<Goal> syncProgressFromHabit(Long id, String userId) {
        Optional<Goal> goalOpt = goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId));
        
        if (goalOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Goal goal = goalOpt.get();
        
        try {
            // Obtener el progreso actual del hábito
            Integer habitCompletions = habitsClient.getHabitCompletionCount(goal.getHabitId(), userId);
            
            if (habitCompletions != null) {
                goal.setCurrentProgress(habitCompletions);
                
                // Verificar si completó el objetivo
                if (habitCompletions >= goal.getTargetValue() && goal.getStatus() == Goal.GoalStatus.ACTIVE) {
                    goal.setStatus(Goal.GoalStatus.COMPLETED);
                    goal.setCompletedAt(java.time.LocalDateTime.now());
                    log.info("Goal {} auto-completed after sync. Completions: {}", id, habitCompletions);
                }
                
                return Optional.of(goalRepository.save(goal));
            }
        } catch (FeignException e) {
            log.error("Error syncing progress from habit service: {}", e.getMessage());
        }
        
        return Optional.of(goal);
    }

    @Transactional
    public Optional<Goal> markAsCompleted(Long id, String userId) {
        return goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId))
                .map(goal -> {
                    goal.setStatus(Goal.GoalStatus.COMPLETED);
                    goal.setCompletedAt(java.time.LocalDateTime.now());
                    log.info("Goal {} manually marked as completed by user {}", id, userId);
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public Optional<Goal> markAsFailed(Long id, String userId) {
        return goalRepository.findById(id)
                .filter(goal -> goal.getUserId().equals(userId))
                .map(goal -> {
                    goal.setStatus(Goal.GoalStatus.FAILED);
                    log.info("Goal {} marked as failed by user {}", id, userId);
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public void checkAndUpdateOverdueGoals(String userId) {
        List<Goal> overdueGoals = goalRepository.findOverdueGoals(userId, LocalDate.now());
        
        for (Goal goal : overdueGoals) {
            if (goal.getCurrentProgress() >= goal.getTargetValue()) {
                goal.setStatus(Goal.GoalStatus.COMPLETED);
                goal.setCompletedAt(java.time.LocalDateTime.now());
            } else {
                goal.setStatus(Goal.GoalStatus.FAILED);
            }
            goalRepository.save(goal);
        }
        
        if (!overdueGoals.isEmpty()) {
            log.info("Updated {} overdue goals for user {}", overdueGoals.size(), userId);
        }
    }

    public Long getGoalCountByStatus(String userId, Goal.GoalStatus status) {
        return goalRepository.countByUserIdAndStatus(userId, status);
    }

    // Método interno para obtener metas por habitId sin userId (para llamadas entre servicios)
    public List<Goal> getGoalsByHabitId(Long habitId) {
        return goalRepository.findByHabitId(habitId);
    }

    // Método para eliminar todas las metas asociadas a un hábito (cuando se elimina el hábito)
    @Transactional
    public int deleteGoalsByHabitId(Long habitId) {
        List<Goal> goals = goalRepository.findByHabitId(habitId);
        int count = goals.size();
        if (count > 0) {
            goalRepository.deleteAll(goals);
            log.info("Deleted {} goals associated with habit {}", count, habitId);
        }
        return count;
    }
}
